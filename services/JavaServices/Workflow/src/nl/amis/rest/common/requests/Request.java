package nl.amis.rest.common.requests;

import javax.ws.rs.QueryParam;

public class Request {
    public Request() {
    }

    @QueryParam("noOfRecords")
    private int noOfRecords;

    @QueryParam("orderBy")
    private String orderBy;

    @QueryParam("search")
    private String search;


    public void setNoOfRecords(int noOfRecords) {
        this.noOfRecords = noOfRecords;
    }

    public int getNoOfRecords() {
        return noOfRecords;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getSearch() {
        return search;
    }
}
