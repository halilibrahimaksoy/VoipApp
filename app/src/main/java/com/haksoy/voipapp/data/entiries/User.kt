import java.io.Serializable

data class User(
    var uid: String? = null,
    var email: String? = null,
    var name: String? = null,
    var info: String? = null,
    var picture: String? = null,
    var gender: String? = null,
    var location: Location? = null,
    var nat: String? = null
) : Serializable