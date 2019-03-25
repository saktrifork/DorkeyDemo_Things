package dk.trifork.smartspy

import com.google.gson.annotations.SerializedName
import org.json.JSONObject

data class ThingObject(
    @SerializedName("uid") val uid: String,
    @SerializedName("activationkey") val activationkey: Number,
    @SerializedName("isclaimed") val isclaimed: Boolean,
    @SerializedName("claimedby") val claimedby: String?)