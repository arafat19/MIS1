package com.athena.mis.exchangehouse.entity


class ExhSanction {
    String name        // required
    String title
    String dob          // string because it can be only year
    String townOfBirth
    String countryOfBirth
    String nationality
    String passportDetails
    String niNumber
    String position
    String address1
    String address2
    String address3
    String address4
    String address5
    String address6
    String postOrZip
    String country
    String otherInformation
    String groupType
    String aliasType
    String regime
    Date createdOn
    Date lastUpdate
    String groupId

    static mapping = {
        createdOn type: 'date'
        lastUpdate type: 'date'
        id generator: 'sequence', params: [sequence: 'exh_sanction_id_seq']
    }

    static constraints = {
        name(nullable: false, maxSize: 500)    // required
        title(nullable: true)
        dob(nullable: true)
        townOfBirth(nullable: true)
        countryOfBirth(nullable: true)
        nationality(nullable: true)
        passportDetails(nullable: true, maxSize: 1000)
        niNumber(nullable: true)
        position(nullable: true)
        address1(nullable: true)
        address2(nullable: true)
        address3(nullable: true)
        address4(nullable: true)
        address5(nullable: true)
        address6(nullable: true)
        postOrZip(nullable: true)
        country(nullable: true)
        otherInformation(nullable: true, maxSize: 1000)
        groupType(nullable: true)
        aliasType(nullable: true)
        regime(nullable: true)
        createdOn(nullable: true)
        lastUpdate(nullable: true)
        groupId(nullable: true)
    }
}
