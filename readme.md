Soa Suite PS6 11.1.1.7 Restful Services
========================================

RestFul API ( Jersey 1.18 ) on Oracle SOA Suite, calls the HumanWorkFlow Java API and SOA identity SAOP Service
- Purchase Service, Soa Suite Human WorkFlow. Assigned to AppAdmin & AppUser roles
- Repair Service, Soa Suite Human WorkFlow. Assigned to AppAdmin & AppUser roles
- Product Service, Dummy Service for some extra product information 
- Identity Service, Java API which connect to the SOA Suite identity service 

Installation
------------
tested on the following Soa Suite vagrant box https://github.com/biemond/vagrant-soasuite

How to get started ( tested on vagrant 1.41 with Virtualbox 4.3.6 )
- Clone or Download from github
- Go the soa vagrant folder
- vagrant up db
- vagtant up admin
- go http://10.10.10.10:7001/console
- use weblogic password = weblogic1
- startup soa_server1

Other useful info
- All boxes has a oracle user account with password oracle
- Database ( service test.oracle.com on 10.10.10.5 )
- All db password have Welcome01 as password
- root password is vagrant

Soa Suite deployments
- start wlst.sh or wlst.bat
- wlst.sh scripts/createUsersAndGroups.py
- connect ( weblogic , weblogic1 , t3://10.10.10.10:7001 )
- Open JDeveloper 11.1.1.7 with Soa Suite plugin
- Open the HWF soa workspace
- Create a Wls / SOA Connection
- Deploy the Purchase / Repair Composites
- Open soapUI and open/import the Purchase and Repair projects
- Do some xbox / ps4 purchase or repair invocations
- Check the EM ( http://10.10.10.10:7001/em ) and see if there are any running instances

JavaServices
- Open in JDeveloper the JavaServices workspace
- Rebuild everything
- Run the QueryPurchase & QueryRepair Test classes ( WorkFlow project , nl.amis.soa.workflow.tasks.test package ) 

Test these services on soa suite server ( soa_server1 )
- deploy Jersey 1.18 shared library ( jersey-bundle-1.18.war to the soa_server1)
- deploy workflow to soa_server1

Test these services on integrated weblogic service
- add javax.el-api-2.2.4.jar to your jre/lib/ext of your jdk home
- enable crossdomain ( wls domain /security )
- Set domain password use weblogic1
- run createUsersAndGroups.py on your admin server
- deploy Jersey 1.18 shared library ( jersey-bundle-1.18.war to the adminserver)
- run a rest service from jdeveloper

Services ( Accept application/json or xml )
- Login Post http://xxxx/Workflow-context-root/login?op=login with payload username=user1&password=weblogic1
- Identity Get http://xxxx/Workflow-context-root/jersey/identity/users/user1 or user2
- Purchase Totals Get http://xxxx/Workflow-context-root/jersey/workflow/users/user1/purchase/total
- Purchases Get http://xxxx/Workflow-context-root/jersey/workflow/users/user1/purchase or ?search=xbox or ?product=xbox&shipper=ups
- Repair Totals Get http://xxxx/Workflow-context-root/jersey/workflow/users/user2/total 
- Repairs Get http://xxxx/Workflow-context-root/jersey/workflow/users/user2/repair?product=xbox



