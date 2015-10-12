package com.athena.mis.sarb.actions.customerdetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger

/**
 * Validate photoId of customer
 * For details go through use case 'ValidateCustomerPhotoIdActionService'
 */
class ValidateSarbCustomerPhotoIdNoActionService extends BaseService implements ActionIntf {

    private static final String INVALID_PHOTO_ID_NO = "Invalid Photo ID no"
    private static final String NATIONALITY_CHANGE = "Nationality can't be South-African for Foreign passport"
    private static final String SOUTH_AFRICA_CODE = "ZA"

    private static final String NID_CODE = "IDNumber"
    private static final String TEMP_RES_CODE = "TempResPermitNumber"
    private static final String PASSPORT_CODE = "ForeignIDNumber"
    private static List<String> lstPhotoIdCodes = [NID_CODE,TEMP_RES_CODE,PASSPORT_CODE]


	private Logger log = Logger.getLogger(getClass())

	public Object executePreCondition(Object parameters, Object obj) {
		return null
	}

	public Object executePostCondition(Object parameters, Object obj) {
		return null
	}
	/**
	 * Validate photoIdNo of customer based on SARB validation logic
	 * @param parameters - photoIdNo
	 * @param obj - N/A
	 * @return - isError
	 */
	public Object execute(Object parameters, Object obj) {
		Map result = new LinkedHashMap()
		try {
            Map params = (Map) parameters
			String photoIdNo = (String) params.photoIdNo
            String photoTypeCode = (String) params.customerPhotoTypeCode
            String countryCode = (String) params.countryCode

            if(!lstPhotoIdCodes.contains(photoTypeCode)) {
                result.put(Tools.IS_ERROR, true)
                result.put(Tools.MESSAGE, INVALID_PHOTO_ID_NO)
                return result
            }
            if(countryCode.equalsIgnoreCase(SOUTH_AFRICA_CODE) && photoTypeCode.equals(PASSPORT_CODE)) {
                result.put(Tools.IS_ERROR, true)
                result.put(Tools.MESSAGE, NATIONALITY_CHANGE)
                return result
            }
            if(!photoTypeCode.equals(NID_CODE)) {       // no validation except National ID
                result.put(Tools.IS_ERROR, false)
                return result
            }
			if (!isValidPhotoIdNo(photoIdNo)) {
				result.put(Tools.IS_ERROR, true)
                result.put(Tools.MESSAGE, INVALID_PHOTO_ID_NO)
				return result
			}
			result.put(Tools.IS_ERROR, false)
			return result
		}
		catch (Exception ex) {
			log.error(ex.getMessage())
			result.put(Tools.IS_ERROR, true)
			result.put(Tools.MESSAGE, INVALID_PHOTO_ID_NO)
			return result
		}
	}

	public Object buildSuccessResultForUI(Object obj) {
		return null
	}

	public Object buildFailureResultForUI(Object obj) {
		return null
	}

	/**
	 * 1. check if null
	 * 2. check length<=2
	 * 3. get last number as controlling number
	 * 4. get sum of odd position numbers
	 * 5. get sum of (even position numbers X 2) [before sum number->16 will be 1+6=7]
	 * 6. subtract summation of that 2 sum from (nearest multiplier of 10)
	 * 7. if subtract result == controlling number -> return true; else false
	 */
	private boolean isValidPhotoIdNo(String photoIdNo) {
		if (!photoIdNo) return false
		int length = photoIdNo.length()
		if (length <= 2) return false
		if (length % 2 != 1) {
			return false
		}
		int controllingFigure = Integer.parseInt(photoIdNo[length - 1])
		List<Integer> lstOddPosition = getOddPositionNumbers(photoIdNo)
		List<Integer> lstEvenPosition = getEvenPositionNumbers(photoIdNo)
		int oddPositionSum = (int) lstOddPosition.sum()
		int evenPositionSum = getEvenPositionSum(lstEvenPosition)
		int totalSum = oddPositionSum + evenPositionSum
		int nextMultiplyTen = getNextMultiplyOfTen(totalSum)
		int subtract = nextMultiplyTen - totalSum
		if (controllingFigure == subtract) {
			return true
		}
		return false
	}

	private List<Integer> getOddPositionNumbers(String photoId) {
		List<Integer> oddPositionNumbers = []
		for (int i = 0; i < photoId.length() - 1; i += 2) {
			oddPositionNumbers << Integer.parseInt(photoId[i])
		}
		return oddPositionNumbers
	}

	private List<Integer> getEvenPositionNumbers(String photoId) {
		List<Integer> evenPositionNumbers = []
		for (int i = 1; i < photoId.length() - 1; i += 2) {
			evenPositionNumbers << Integer.parseInt(photoId[i])
		}
		return evenPositionNumbers
	}

	private int getEvenPositionSum(List<Integer> numbers) {
		int sum = 0
		for (int i = 0; i < numbers.size(); i++) {
			int num = numbers[i].intValue()
			num *= 2
			if (num > 9) {
				int num1 = num / 10
				int num2 = num % 10
				num = num1 + num2
			}
			sum += num
		}
		return sum
	}

	private int getNextMultiplyOfTen(int number) {
		if (number % 10 > 0) {
			number = number + (10 - (number % 10))
		}
		return number
	}
}
