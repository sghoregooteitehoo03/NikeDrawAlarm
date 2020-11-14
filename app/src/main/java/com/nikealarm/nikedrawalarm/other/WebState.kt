package com.nikealarm.nikedrawalarm.other

object WebState {
    const val WEB_LOGIN = "WEB_LOGIN" // 로그인
    const val WEB_AFTER_LOGIN = "WEB_AFTER_LOGIN" // 로그인 후
    const val WEB_SELECT_SIZE = "WEB_SELECT_SIZE" // 신발 사이즈 선택
    const val WEB_SUCCESS = "WEB_SUCCESS" // 응모 성공

    const val WEB_FAIL = "WEB_FAIL" // 실패

    const val ERROR_LOGIN = "아이디 및 비밀번호가 잘못되었습니다."
    const val ERROR_SIZE = "선택하신 사이즈가 존재하지 않습니다."
    const val ERROR_END_DRAW = "이미 응모가 종료되었습니다."
    const val ERROR_OTHER = "자동응모 과정중 오류가 발생하였습니다."
    const val NOT_ERROR = "NOT_ERROR"
}