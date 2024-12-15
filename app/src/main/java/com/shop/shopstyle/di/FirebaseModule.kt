package com.shop.shopstyle.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase{
        return FirebaseDatabase
            .getInstance("https://shopstyle-c95bc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    }

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore{
        return FirebaseFirestore.getInstance()
    }
}