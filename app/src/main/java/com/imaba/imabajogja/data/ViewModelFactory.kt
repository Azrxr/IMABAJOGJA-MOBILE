package com.imaba.imabajogja.data

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.imaba.imabajogja.data.di.Injection
import com.imaba.imabajogja.data.repository.LoginRepository
import com.imaba.imabajogja.data.repository.MemberRepository
import com.imaba.imabajogja.ui.MainViewModel
import com.imaba.imabajogja.ui.authentication.AuthViewModel
import com.imaba.imabajogja.ui.home.HomeViewModel

class ViewModelFactory(
    private val loginRepository: LoginRepository
    ) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(loginRepository) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(loginRepository) as T
            }


            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(
                        Injection.provideRepository(context)
                    )
                }
            }
            return INSTANCE as ViewModelFactory
        }

    }
}