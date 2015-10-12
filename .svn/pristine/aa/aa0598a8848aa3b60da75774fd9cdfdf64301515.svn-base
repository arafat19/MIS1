package com.athena.mis.exchangehouse.common

import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools

class WrapSanction {

    private static final int IDX_NAME_6 = 0
    private static final int IDX_NAME_1 = 1
    private static final int IDX_NAME_2 = 2
    private static final int IDX_NAME_3 = 3
    private static final int IDX_NAME_4 = 4
    private static final int IDX_NAME_5 = 5

    private static final int IDX_TITLE = 6
    private static final int IDX_DOB = 7
    private static final int IDX_TOWN_OF_BIRTH = 8
    private static final int IDX_COUNTRY_OF_BIRTH = 9
    private static final int IDX_NATIONALITY = 10

    private static final int IDX_PASSPORT_DETAILS = 11
    private static final int IDX_NI_NUMBER = 12
    private static final int IDX_POSITION = 13

    private static final int IDX_ADDRESS_1 = 14
    private static final int IDX_ADDRESS_2 = 15
    private static final int IDX_ADDRESS_3 = 16
    private static final int IDX_ADDRESS_4 = 17
    private static final int IDX_ADDRESS_5 = 18
    private static final int IDX_ADDRESS_6 = 19

    private static final int IDX_POST_ZIP_CODE = 20
    private static final int IDX_COUNTRY = 21
    private static final int IDX_OTHER_INFO = 22
    private static final int IDX_GROUP_TYPE = 23
    private static final int IDX_ALIAS_TYPE = 24
    private static final int IDX_REGIME = 25
    private static final int IDX_CREATED_ON = 26
    private static final int IDX_LAST_UPDATED = 27
    private static final int IDX_GROUP_ID = 28


    Long serial  // maintain a serial within wrapper (main file doesn't have any serial)

    String name        // combination of six names: name6, name1, name2..., name5
    String name6
    String name1
    String name2
    String name3
    String name4
    String name5
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
    String strCreatedOn
    Date lastUpdate
    String strLastUpdate
    String groupId

    List errors = []


    private static final String ERROR_PARSING_CSV = "Error occurred to parse the CSV file"
    private static final String NAME_REQUIRED = "Any one name is required"
    private static final String NAME_TOO_LONG = "Names are too long"
    private static final String CREATE_DATE_REQUIRED = "Sanction listing date is required"
    private static final String CREATE_DATE_INVALID = "Listing date format invalid (expected: dd/MM/yyyy)"
    private static final String UPDATE_DATE_REQUIRED = "Sanction update date is required"
    private static final String UPDATE_DATE_INVALID = "Update date format invalid (expected: dd/MM/yyyy)"




    WrapSanction(def tokens, long rowCount) {

        try {
            this.serial = rowCount

            this.name6 = tokens[WrapSanction.IDX_NAME_6]
            this.name1 = tokens[WrapSanction.IDX_NAME_1]
            this.name2 = tokens[WrapSanction.IDX_NAME_2]
            this.name3 = tokens[WrapSanction.IDX_NAME_3]
            this.name4 = tokens[WrapSanction.IDX_NAME_4]
            this.name5 = tokens[WrapSanction.IDX_NAME_5]

            this.name = concatNames()   // concat all 6 names with comma delimitation

            this.title = tokens[WrapSanction.IDX_TITLE]
            this.dob = tokens[WrapSanction.IDX_DOB]
            this.townOfBirth = tokens[WrapSanction.IDX_TOWN_OF_BIRTH]
            this.countryOfBirth = tokens[WrapSanction.IDX_COUNTRY_OF_BIRTH]
            this.nationality = tokens[WrapSanction.IDX_NATIONALITY]

            this.passportDetails = tokens[WrapSanction.IDX_PASSPORT_DETAILS]
            this.niNumber = tokens[WrapSanction.IDX_NI_NUMBER]
            this.position = tokens[WrapSanction.IDX_POSITION]

            this.address1 = tokens[WrapSanction.IDX_ADDRESS_1]
            this.address2 = tokens[WrapSanction.IDX_ADDRESS_2]
            this.address3 = tokens[WrapSanction.IDX_ADDRESS_3]
            this.address4 = tokens[WrapSanction.IDX_ADDRESS_4]
            this.address5 = tokens[WrapSanction.IDX_ADDRESS_5]
            this.address6 = tokens[WrapSanction.IDX_ADDRESS_6]

            this.postOrZip = tokens[WrapSanction.IDX_POST_ZIP_CODE]
            this.country = tokens[WrapSanction.IDX_COUNTRY]
            this.otherInformation = tokens[WrapSanction.IDX_OTHER_INFO]
            this.groupType = tokens[WrapSanction.IDX_GROUP_TYPE]
            this.aliasType = tokens[WrapSanction.IDX_ALIAS_TYPE]
            this.regime = tokens[WrapSanction.IDX_REGIME]
            this.strCreatedOn = tokens[WrapSanction.IDX_CREATED_ON]
            this.strLastUpdate = tokens[WrapSanction.IDX_LAST_UPDATED]
            this.groupId = tokens[WrapSanction.IDX_GROUP_ID]


        } catch (Exception ex) {
            errors << ERROR_PARSING_CSV
        }
    }

    private String concatNames() {
        String strName = Tools.EMPTY_SPACE
        if (this.name6.length() > 0) {
            strName = strName + this.name6
        }
        if (this.name1.length() > 0) {
            if (strName.length() > 0) {
                strName = strName + Tools.EMPTY_SPACE_COMA + this.name1
            } else {
                strName = strName + this.name1
            }
        }

        if (this.name2.length() > 0) {
            if (strName.length() > 0) {
                strName = strName + Tools.EMPTY_SPACE_COMA + this.name2
            } else {
                strName = strName + this.name2
            }
        }

        if (this.name3.length() > 0) {
            if (strName.length() > 0) {
                strName = strName + Tools.EMPTY_SPACE_COMA + this.name3
            } else {
                strName = strName + this.name3
            }
        }

        if (this.name4.length() > 0) {
            if (strName.length() > 0) {
                strName = strName + Tools.EMPTY_SPACE_COMA + this.name4
            } else {
                strName = strName + this.name4
            }
        }

        if (this.name5.length() > 0) {
            if (strName.length() > 0) {
                strName = strName + Tools.EMPTY_SPACE_COMA + this.name5
            } else {
                strName = strName + this.name5
            }
        }
        return strName.trim().toLowerCase()
    }

    private String validateFullName() {
        if (this.name == Tools.EMPTY_SPACE) {
            return NAME_REQUIRED
        } else if (this.name.length() > 255) {
            return NAME_TOO_LONG
        }
        return null
    }

    private String validateCreateDate() {
        if (this.strCreatedOn == Tools.EMPTY_SPACE) {
            return CREATE_DATE_REQUIRED
        }
        Date dt = DateUtility.parseMaskedDate(strCreatedOn)
        if (!dt) {
            return CREATE_DATE_INVALID
        } else {
            this.createdOn = dt
        }
        return null
    }

    private String validateUpdateDate() {
        if (this.strLastUpdate == Tools.EMPTY_SPACE) {
            return UPDATE_DATE_REQUIRED
        }
        Date dt = DateUtility.parseMaskedDate(strLastUpdate)
        if (!dt) {
            return UPDATE_DATE_INVALID
        } else {
            this.lastUpdate = dt
        }
        return null
    }

    boolean validateFile() {

        String tempError = null

        // Validate name
        tempError = validateFullName()
        if (tempError) errors << tempError
        // Validate create date
        tempError = validateCreateDate()
        if (tempError) errors << tempError
        // Validate last update date
        tempError = validateUpdateDate()
        if (tempError) errors << tempError

        //*********************** Now all csv file validation check is done!

        formatEmptyProperties()

        return (errors.size() == 0)
    }

    private formatEmptyProperties() {

        if (this.title.length() == 0) {
            this.title = null
        }
        if (this.dob.length() == 0) {
            this.dob = null
        }
        if (this.townOfBirth.length() == 0) {
            this.townOfBirth = null
        }
        if (this.countryOfBirth.length() == 0) {
            this.countryOfBirth = null
        }
        if (this.nationality.length() == 0) {
            this.nationality = null
        }
        if (this.passportDetails.length() == 0) {
            this.passportDetails = null
        }
        if (this.niNumber.length() == 0) {
            this.niNumber = null
        }
        if (this.position.length() == 0) {
            this.position = null
        }
        if (this.address1.length() == 0) {
            this.address1 = null
        }
        if (this.address2.length() == 0) {
            this.address2 = null
        }
        if (this.address3.length() == 0) {
            this.address3 = null
        }
        if (this.address4.length() == 0) {
            this.address4 = null
        }
        if (this.address5.length() == 0) {
            this.address5 = null
        }
        if (this.address6.length() == 0) {
            this.address6 = null
        }
        if (this.postOrZip.length() == 0) {
            this.postOrZip = null
        }
        if (this.country.length() == 0) {
            this.country = null
        }
        if (this.otherInformation.length() == 0) {
            this.otherInformation = null
        }
        if (this.groupType.length() == 0) {
            this.groupType = null
        }
        if (this.aliasType.length() == 0) {
            this.aliasType = null
        }
        if (this.regime.length() == 0) {
            this.regime = null
        }
        if (this.groupId.length() == 0) {
            this.groupId = null
        }
    }
}
