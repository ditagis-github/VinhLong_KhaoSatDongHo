package vinhlong.ditagis.com.khaosatdongho.entities.entitiesDB

class UserDangNhap private constructor() {
    var user: User? = null

    companion object {

        private var instance: UserDangNhap? = null

        fun getInstance(): UserDangNhap {
            if (instance == null) {
                instance = UserDangNhap()
            }
            return instance as UserDangNhap
        }
    }

}
