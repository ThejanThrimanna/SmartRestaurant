package com.thejan.proj.restaurant.tablet.android.helper

const val CURRENCY = "Rs"

//tables
const val TABLE_CART = "cart"
const val TABLE_FOOD_CATEGORY = "food_category"
const val TABLE_FOOD_TYPE = "food_type"
const val TABLE_MENU = "menu"
const val TABLE_USERS = "users"
const val TABLE_CART_ITEMS = "cart_items"
const val TABLE_TABLES = "table"
const val TABLE_ADMIN_USERS = "admin_users"
const val TABLE_TABLE_RESERVATION = "table_reservation"
const val TABLE_OFFER = "offer"

//columns
const val COLUMN_CART_ID = "cartId"
const val COLUMN_FOOD_ID = "foodId"
const val COLUMN_CART_ITEM_ID = "cartItemId"
const val COLUMN_PHONE = "phone"
const val COLUMN_DEVICE_ID = "deviceId"
const val COLUMN_TABLE_NUMBER = "tableNumber"
const val COLUMN_NUMBER_OF_SEATS = "numberOfSeats"
const val COLUMN_BOOKING = "booking"
const val COLUMN_STATUS = "status"
const val COLUMN_IS_ACTIVE = "isActive"
const val COLUMN_EMP_ID = "emp_id"
const val COLUMN_PASSWORD = "password"
const val COLUMN_USER_ROLE = "role"
const val COLUMN_CATEGORY = "cat"
const val COLUMN_COST = "cost"
const val COLUMN_DESC = "desc"
const val COLUMN_MENU_ID = "id"
const val COLUMN_ID = "id"
const val COLUMN_IMAGE = "image"
const val COLUMN_NAME = "name"
const val COLUMN_PRICE = "price"
const val COLUMN_TYPE = "type"
const val COLUMN_DATE = "date"
const val COLUMN_CHEF = "chef"
const val COLUMN_AMOUNT = "amount"
const val COLUMN_CHEF_NAME = "chef_name"
const val COLUMN_ADDED = "added"
const val COLUMN_COUNT = "count"
const val COLUMN_OFFER_ID= "offer_id"
const val COLUMN_PERCENTAGE= "percentage"
const val COLUMN_START_DATE= "start_date"
const val COLUMN_END_DATE= "end_date"
const val COLUMN_OFFER_NAME= "offername"
const val COLUMN_DISCOUNT= "dicount"
const val COLUMN_IS_OFFER= "isoffer"

//common
const val ID_CART = "CART"
const val ID_CART_ITEM = "CART_ITEM"

//status
const val STATUS_PLACE_AN_ORDER = "Place an Order"
const val STATUS_PENDING = "Pending"
const val STATUS_PROCESSING = "Processing"
const val STATUS_SERVED = "Served"
const val STATUS_BILL_SETTLEMENT_PENDING = "Bill Settlement Pending"
const val STATUS_SETTLED = "Bill Settled"
const val STATUS_CANCELED = "Order Canceled"

//activity Results
const val ADD_NEW_FOOD = 101
const val ADD_NEW_CATEGORY = 102
const val RE_ORDER = 103

const val STORAGE_PERMISSION_REQUEST = 333
const val IMAGE_PICKER_REQUEST = 335
const val CAMERA_REQUEST = 337
const val CAMERA_PERMISSION_REQUEST = 131
const val INVALID_IMAGE = "invalid_image"

//Date Time Formats
const val FORMAT_1 = "yyyy/MM/dd hh:mm a"
const val FORMAT_2 = "yyyy/MM/dd"
const val FORMAT_3 = "hh:mm a"
const val FORMAT_4 = "yyyy/MM/dd HH:mm"

//Intent extras
const val IS_ADD = "isAdd"
const val OBJECT = "object"
const val STATUS = "status"

//Type
const val VEG = "type_1"
const val NON_VEG = "type_2"

const val SECRET_KEY = "tK5UTui+DPh8lIlBxya5XVsmeDCoUl6vHhdIESMB6sQ="
const val SALT = "QWlGNHNhMTJTQWZ2bGhpV3U=" // base64 decode => AiF4sa12SAfvlhiWu
const val IV = "bVQzNFNhRkQ1Njc4UUFaWA==" // base64 decode => mT34SaFD5678QAZX