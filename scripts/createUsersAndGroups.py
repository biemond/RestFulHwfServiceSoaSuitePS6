

connect()

try:
    atnr=cmo.getSecurityConfiguration().getDefaultRealm().lookupAuthenticationProvider('DefaultAuthenticator')

    print 'create group AppAdmin'
    atnr.createGroup('AppAdmin','AppAdmin')

    print 'create group AppUser'
    atnr.createGroup('AppUser','AppUser')

    print 'create user: ','humantask'
    atnr.createUser('humantask','weblogic1','humantask')
    atnr.addMemberToGroup('Administrators','humantask')

    print 'create user: ','user1'
    atnr.createUser('user1','weblogic1','user1')
    atnr.addMemberToGroup('AppUser','user1')

    print 'create user: ','user2'
    atnr.createUser('user2','weblogic1','user2')
    atnr.addMemberToGroup('AppAdmin','user2')
    atnr.addMemberToGroup('AppUser' ,'user2')

except:
    print "Unexpected error:", sys.exc_info()[0]
