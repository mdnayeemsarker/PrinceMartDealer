package com.princemartbd.dealer.helper;

public class Constant {
    //MODIFICATION PART

    //Admin panel url
    public static final String MainBaseUrl = "https://admin.princemartbd.com/";
    public static String APP_UPDATE_URL = MainBaseUrl + "apps.php";
//    public static final String MainBaseUrl = "https://beta.princemartbd.com/";

    //set your jwt secret key here...key must same in PHP and Android
    public static final String JWT_KEY = "BFbHzx@?6mU}_wv8Q2q~A*&aWp%39=GTVf!;t)e(Ey{<j^LR:,";  // change

    //MODIFICATION PART END
    public static final int LOAD_ITEM_LIMIT = 10;

    public static final String BaseUrl = MainBaseUrl + "dealer/";
    public static final String BaseUrl_API_Firebase = MainBaseUrl + "api-firebase/";

    //**********APIS**********

    public static final String AUTH_LOGIN_URL = BaseUrl + "auth.php";
    public static final String ADD_EMPLOYEE_URL = BaseUrl + "manage_employee.php";
    public static final String WITHDRAW_REQUEST = BaseUrl + "withdraw.php";
    public static final String DASHBOARD_URL = BaseUrl + "dashboard.php";
    public static final String ALL_EMPLOYEES_URL = BaseUrl + "manage_employee.php";
    public static final String ALL_TRANSACTIONS = BaseUrl + "manage_transactions.php";

    public static final String GET_SELLER_DATA_URL = BaseUrl_API_Firebase + "get-seller-data.php";
    public static final String SETTING_URL = BaseUrl_API_Firebase + "settings.php";

    public static final String GET_TOKEN = BaseUrl + "get_token.php";
    public static final String LOCATION = BaseUrl_API_Firebase + "get-locations.php";
//    public static final String LOCATION = "http://beta.princemartbd.com/api-firebase/get-locations.php";

    //**************parameters***************
    public static final String AccessKey = "accesskey";
    public static final String AccessKeyVal = "j-V]![e7f6k8)W&r(v=pQs<Zm.u?^y4dUC;c,qa5RbBLP3}X%h";  // change
    public static final String PROFILE = "profile";
    public static final String GetVal = "1";
    public static final String AUTHORIZATION = "Authorization";
    public static final String IS_USER_LOGIN = "is_user_login";
    public static final String GET_PRIVACY = "get_privacy";
    public static final String GET_TERMS = "get_terms";
    public static final String GET_CONTACT = "get_contact";
    public static final String GET_ABOUT_US = "get_about_us";
    public static final String BALANCE = "balance";
    public static final String COUNTRY_CODE = "country_code";
    public static final String SUCCESS_TRAN = "SUCCESS";
    public static final String CREDIT = "credit";
    public static final String TYPE = "type";
    public static final String TYPE_ID = "type_id";
    public static final String LAST_UPDATED = "last_updated";
    public static final String DATE_CREATED = "date_created";
    public static final String NAME = "name";
    public static final String ID = "id";
    public static final String ADDED_BY = "added_by";
    public static final String ADDER_TYPE = "adder_type";
    public static final String STATUS = "status";
    public static final String EMAIL = "email";
    public static final String MOBILE = "mobile";
    public static final String ADDRESS = "address";
    public static final String LOGIN = "login";
    public static final String PAGE = "page";
    public static final String PASSWORD = "password";
    public static final String FCM_ID = "fcm_id";
    public static final String ERROR = "error";
    public static final String USER_ID = "user_id";
    public static final String ALL_EMPLOYEES = "all_employees"; //
    public static final String ADD_EMPLOYEES = "add_employee";
    public static final String FORGOT_PASSWORD_MOBILE = "forgot-password-mobile";
    public static final String DATA = "data";
    public static final String CURRENCY = "currency";
    public static final String AMOUNT = "amount";
    public static final String MESSAGE = "message";
    public static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghjiklmnopqrstuvwxyz";
    public static final String NUMERIC_STRING = "123456789";
    public static final String EMPLOYEES = "employees";
    public static final String API_KEY = "API_KEY";
    public static final String NOT_APPROVED = "NOT APPROVED";
    public static final String APPROVED = "APPROVED";
    public static final String PENDING = "PENDING";
    public static final String DEACTIVATE = "DEACTIVATE";
    public static final String CREATED = "CREATED";
    public static final String BLOCK = "BLOCK";
    public static final String CANCEL = "CANCEL";
    public static final String GET_TOKEN_KEY = "get_nh_key";
    public static final String VERIFY_USER = "verify-user";
    public static final String UPDATE_NAME = "update_name";
    public static final String DEALER_TYPE = "dealer_type";
    public static final String EMPLOYEE = "employee";
    public static final String DEALER = "dealer";
    public static final String CREATED_AT = "created_at";
    public static final String SEARCH = "search";
    public static final String TOTAL = "total";
    public static final String GET_CITIES = "get_cities";
    public static final String GET_AREAS = "get_areas";
    public static final String GET_PINCODE = "get_pincode";
    public static final String CITY = "City";
    public static final String AREA = "Area";
    public static final String PINCODE = "pincode";
    public static final String OFFSET = "offset";
    public static final String LIMIT = "limit";
    public static final String CITY_ID = "city_id";
    public static final String UPDATE_ADDRESS = "update_address";
    public static final String PINCODE_ID = "pincode_id";
    public static final String AREA_ID = "area_id";
    public static final String TOTAL_BALANCE = "totalBalance";
    public static final String TODAY = "todayEarning";
    public static final String YESTERDAY = "yesterdayEarning";
    public static final String WEEKLY = "weeklyEarning";
    public static final String MONTHLY = "monthlyEarning";
    public static final String LAST_MONTH = "lastMonthlyEarning";
    public static final String LAST_3_MONTH = "last3MonthlyEarning";
    public static final String YEARLY = "yearlyEarning";
    public static final String LAST_YEAR = "lastYearlyEarning";
    public static final String ABMN_EMP_ADD = "abmn_emp_add";
    public static final String ABMN_EMP_ALL = "abmn_emp_all";
    public static final String ABMN_ALL_TRAN = "all_transactions";
    public static final String ABMN_SUCCESS_TRAN = "success_transactions";
    public static final String ABMN_CANCELED_TRAN = "canceled_transactions";
    public static final String ABMN_NEW_TRAN = "new_transactions";
    public static final String ABMN_PENDING_TRAN = "pending_transactions";
    public static final String ABMN_WITH_REQUEST = "withdraw_request";
    public static final String ABMN_WITH_HISTORY = "withdraw_history";
    public static final String TRAN_DETAILS_ITEM = "transaction";
    public static final String ABMN = "abmn";
    public static final String UPDATE_EMPLOYEE = "update_employee";
    public static final String EMPLOYEE_ID = "employee_id";
    public static final String EMPLOYEE_NAME = "employee_name";
    public static final String CHANGE_PASSWORD = "change_password";
    public static final String DELETE_EMPLOYEE = "delete_employee";
    public static final String SELLER_ID = "seller_id";
    public static final String DEALER_ID = "dealer_id";
    public static final String TRX = "trx";
    public static final String ACCEPTED_AT = "accepted_at";
    public static final String UPDATED_AT = "updated_at";
    public static final String SELLER_NAME = "seller_name";
    public static final String SEND_REQUEST = "send_request";
    public static final String GET_REQUEST = "get_requests";
    public static final String SETTINGS = "settings";
    public static final String ABMN_QR_RESULT = "abmn_qr_result";
    public static final String TRANSFER = "transfer";
    public static final String SLUG = "slug";
    public static final String ERROR_TRAN = "ERROR";
    public static final String REQUESTED = "REQUESTED";
    public static final String UPDATE_TRANSACTION = "update_transaction";
    public static final String TRANSACTION_ID = "transaction_id";
    public static final String OTP = "otp";
    public static final String WITHDRAW = "withdraw";
    public static final String EARNING = "earning";
    public static final String EMP = "emp";
    public static final String TRAN = "tran";
    public static final String WITH = "with";
    public static final String GET_SELLER_DATA = "get_seller_data";
    public static final String STORE_NAME = "store_name";
    public static final String SELLER_ADDRESS = "seller_address";
    public static final String TEXT = "text";
    public static final String GENERATED_OTP = "generated_otp";
    public static final String SEND_SMS = "send_sms";
    public static final String SMS_SEND_URL = BaseUrl_API_Firebase + "sms.php";

    //**************Constants Values***************
    public static int TOTAL_CART_ITEM = 0;
    public static String MERCHANT_ID = "";
    public static String MERCHANT_KEY = "";
    public static String MERCHANT_SALT = "";
    public static String GET_CON_VAL_1 = "1";

    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    public static String randomNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * NUMERIC_STRING.length());
            builder.append(NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }
}