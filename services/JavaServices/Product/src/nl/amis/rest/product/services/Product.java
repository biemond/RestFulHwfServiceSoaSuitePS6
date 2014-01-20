package nl.amis.rest.product.services;


import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import nl.amis.rest.product.entities.FileDetail;

import nl.amis.rest.product.entities.ImportProgress;
import nl.amis.rest.product.entities.Status;
import nl.amis.rest.product.entities.UploadRequest;

import org.apache.commons.io.FileUtils;

import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;

@Path("/products")
public class Product {
    public Product() {
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    public Response getProducts() {
        try {
          List<nl.amis.rest.product.entities.Product> products = new ArrayList(); 
          nl.amis.rest.product.entities.Product xbox = new nl.amis.rest.product.entities.Product();
          xbox.setName("xbox");
          xbox.setDescription("Xbox one");  
          xbox.setOnStock(100);  
          products.add(xbox);
          nl.amis.rest.product.entities.Product playstation = new nl.amis.rest.product.entities.Product();
          playstation.setName("playstation"); 
          playstation.setDescription("Sony Playstation 4"); 
          playstation.setOnStock(50);  
          products.add(playstation);
                   
          return  Response.ok( products.toArray( new nl.amis.rest.product.entities.Product[products.size()])).build();
        }  catch (Throwable t) {
            t.printStackTrace();
          return Response.status(Response.Status.NOT_ACCEPTABLE).build();   
        }

    }

    @GET
    @Path("/{product}")
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    public Response getProduct( @PathParam("product")  String name) {
        try {
            
          nl.amis.rest.product.entities.Product product = new nl.amis.rest.product.entities.Product();
          if ( "xbox".equalsIgnoreCase(name)) {
            product.setName("xbox");
            product.setDescription("Xbox one");  
            product.setOnStock(100);  

          }  else if ("playstation".equalsIgnoreCase(name)) {
            product.setName("playstation");    
            product.setDescription("Sony Playstation 4"); 
            product.setOnStock(50);  

          } else {
              return Response.status(Response.Status.NOT_ACCEPTABLE).build(); 
          }
                   
          return  Response.ok( product ).build();
        }  catch (Throwable t) {
            t.printStackTrace();
          return Response.status(Response.Status.NOT_ACCEPTABLE).build();   
        }

    }

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(
        @FormDataParam("fileUpload") InputStream uploadedInputStream,
        @FormDataParam("fileUpload") FormDataContentDisposition fileDetail) {

        if ( fileDetail == null ) {
            Response.status(Response.Status.BAD_REQUEST).build() ; 
        }
            
        String batchId = UUID.randomUUID().toString();
        System.out.println("batchId: "+batchId);
        File tmpdir  = new File(System.getProperty("java.io.tmpdir"));
        System.out.println("tmpdir: "+tmpdir.getAbsolutePath());
        System.out.println("fileDetail: "+fileDetail.getFileName());
        File tmpfile = new File(tmpdir, batchId + "_" +fileDetail.getFileName());
        try {
            FileUtils.copyInputStreamToFile(uploadedInputStream, tmpfile);
        } catch (IOException e) {
           return Response.serverError().build() ;
        }
        FileDetail fileResponse = new FileDetail();
        fileResponse.setFileName(fileDetail.getFileName());
        fileResponse.setBatchId(batchId);
        fileResponse.setFullPath(tmpfile.getAbsolutePath());

        return Response.ok(fileResponse).build();
    }

    @POST
    @Path("/import")
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    public Response importFiles( UploadRequest request) {
        if ( request == null )
           return Response.status(Response.Status.BAD_REQUEST).build() ; 
        // send import started
        Status status = new Status();
        status.setImportStatus("Error");

        Broadcaster b = BroadcasterFactory.getDefault().lookup("product", true);
            
        if ( "product".equalsIgnoreCase(request.getType()) ) {
          status = this.importProduct( request, status,b);
        } else {
            status.setMessage("unknown import action");    
            return Response.status(Response.Status.BAD_REQUEST).build() ; 
        }
        return Response.ok(status).build();
    }

    private Status importProduct(UploadRequest request, Status status, Broadcaster b) {
        if ( request.getFiles() != null ) {

            //InputStream productStream = null;
            List<ImportProgress> progress = new ArrayList();
//            try {              
                for ( int i = 1 ; i <= request.getFiles().size() ; i ++ ) {
                  FileDetail detail = request.getFiles().get(i);  
                  ImportProgress importProduct  = new ImportProgress();
                  importProduct.setBatchId(detail.getBatchId());
                  importProduct.setFileName(detail.getFileName());
                  importProduct.setProgress(i/request.getFiles().size() * 100);
                  
                  //productStream = FileUtils.openInputStream(new File(detail.getFullPath()));
                  
                  b.broadcast(importProduct);
                  sleep();
                }
//            } catch (IOException e) {
//                 status.setMelding(e.getLocalizedMessage());    
//                 return status;
//            }
            status.setImportStatus("Ok");
            status.setImportProgresses(progress);
            return status;
        } else {
            // empty details
            status.setMessage("no files supplied");    
            return status;  
        }
    }

    private void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
    }

}
