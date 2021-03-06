package com.guru.todomvvm.data.db

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Entity(tableName = "todo",indices = [Index(value = ["title", "date"], unique = true)])
@Parcelize()
data class TodoRecord(@PrimaryKey(autoGenerate = true) val id: Long?,
                      @ColumnInfo(name = "title") val title: String,
                      @ColumnInfo(name = "creator") val creator: String,
                      @ColumnInfo(name = "date") val date: String): Parcelable