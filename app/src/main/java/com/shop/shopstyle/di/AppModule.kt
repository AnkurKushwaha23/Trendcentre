package com.shop.shopstyle.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.shop.shopstyle.repository.CartRepository
import com.shop.shopstyle.repository.FavoriteRepository
import com.shop.shopstyle.repository.HomeRepository
import com.shop.shopstyle.repository.OrderRepository
import com.shop.shopstyle.repository.ProfileRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHomeRepository(
        firebaseDatabase: FirebaseDatabase
    ): HomeRepository {
        return HomeRepository(firebaseDatabase)
    }

    @Provides
    @Singleton
    fun provideFavRepository(
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ): FavoriteRepository {
        return FavoriteRepository(firestore, firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideCartRepository(
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ): CartRepository {
        return CartRepository(firestore, firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideOrderRepository(
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ): OrderRepository {
        return OrderRepository(firestore, firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ): ProfileRepository {
        return ProfileRepository(firestore, firebaseAuth)
    }
}