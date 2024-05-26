package com.example.blogappkt.models

import android.os.Parcel
import android.os.Parcelable

data class BlogsModel(
    var heading:String? = "",
    val userName: String? = "",
    val userId: String? = "",
    val date: String? = "",
    var post: String? = "",
    var likeCount: Int? = 0,
    val profileImage: String? = "",
    var isSaved: Boolean = false,
    var postId: String? = "",
    val likedBy: MutableList<String>? = null,

    ) : Parcelable {
    constructor(parcel: Parcel) : this(

        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString() ?: "null",
        parcel.readByte() != 0.toByte(),
        parcel.readString() ?: "null",

        ){}

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(heading)
        parcel.writeString(userName)
        parcel.writeString(userId)
        parcel.writeString(date)
        parcel.writeString(post)
        parcel.writeValue(likeCount)
        parcel.writeString(profileImage)
        parcel.writeByte(if(isSaved) 1 else 0)
        parcel.writeString(postId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BlogsModel> {
        override fun createFromParcel(parcel: Parcel): BlogsModel {
            return BlogsModel(parcel)
        }

        override fun newArray(size: Int): Array<BlogsModel?> {
            return arrayOfNulls(size)
        }
    }
}