package com.thejan.proj.restaurant.admin.android.viewmodel

/**
 * Created by Thejan_Thrimanna on 1/8/19.
 */

class ViewModelState constructor(
    var status: Status,
    var error: Throwable? = null) {
    companion object {

        fun loading(): ViewModelState {
            return ViewModelState(Status.LOADING)
        }

        fun success(): ViewModelState {
            return ViewModelState(Status.SUCCESS)
        }

        fun sub_success1(): ViewModelState {
            return ViewModelState(Status.SUB_SUCCESS1)
        }

        fun cartEmpty(): ViewModelState {
            return ViewModelState(Status.CART_EMPTY)
        }

        fun cartNotEmpty(): ViewModelState {
            return ViewModelState(Status.CART_NOT_EMPTY)
        }

        fun error(): ViewModelState {
            return ViewModelState(Status.ERROR)
        }

        fun validation_issue(): ViewModelState {
            return ViewModelState(Status.VALIDATION_ERROR)
        }

        fun list_empty(): ViewModelState {
            return ViewModelState(Status.LIST_EMPTY)
        }

        fun splashtologin(): ViewModelState {
            return ViewModelState(Status.SPLASH_TO_LOGIN)
        }

        fun splashtohome(): ViewModelState {
            return ViewModelState(Status.SPLASH_TO_HOME)
        }

        fun version_error(): ViewModelState {
            return ViewModelState(Status.VERSION_ERROR)
        }

        fun correct_version(): ViewModelState {
            return ViewModelState(Status.CORRECT_VERSION)
        }

        fun unauthorized(): ViewModelState{
            return ViewModelState(Status.UNAUTHORIZED)
        }

        fun time_out(): ViewModelState{
            return ViewModelState(Status.TIMEOUT)
        }

        fun sub_success2(): ViewModelState {
            return ViewModelState(Status.SUB_SUCCESS2)
        }

        fun sub_success3(): ViewModelState {
            return ViewModelState(Status.SUB_SUCCESS3)
        }

        fun sub_success4(): ViewModelState {
            return ViewModelState(Status.SUB_SUCCESS4)
        }

        fun logout(): ViewModelState {
            return ViewModelState(Status.LOGOUT)
        }

        fun appBlock(): ViewModelState {
            return ViewModelState(Status.APPBLOCK)
        }

        fun appBlockWarning(): ViewModelState {
            return ViewModelState(Status.APPBLOCK_WARNING)
        }
        fun phoneNumberExists(): ViewModelState {
            return ViewModelState(Status.PHONE_NUMBER_EXISTS)
        }
        fun item_exists(): ViewModelState {
            return ViewModelState(Status.ITEM_EXISTS)
        }
    }
}
