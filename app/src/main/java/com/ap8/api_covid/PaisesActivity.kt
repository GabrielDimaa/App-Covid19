package com.ap8.api_covid

import android.os.AsyncTask
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController

class PaisesActivity : AppCompatActivity() {

    private var estatisticasList = mutableListOf<Estatisticas>()
    private var asyncTask: EstatisticasTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paises)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_home)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        carregarEstatisticas()
    }

    private fun carregarEstatisticas() {
        estatisticasList.clear()
        if(asyncTask == null) {
            if(EstatisticasHTTP.hasConnection(this)) {
                if(asyncTask?.status != AsyncTask.Status.RUNNING) {
                    asyncTask = EstatisticasTask()
                    asyncTask?.execute()
                }
            }
        }

    }

    private fun atualizarEstatisticas(resultado: List<Estatisticas>?) {
        if(resultado != null) {
            estatisticasList.clear()
            estatisticasList.addAll(resultado)
        } //else
        asyncTask = null
    }

    inner class EstatisticasTask: AsyncTask<Void, Void, List<Estatisticas>>() {
        override fun doInBackground(vararg params: Void?): List<Estatisticas>? {
            return EstatisticasHTTP.loadEstatisticas()
        }

        override fun onPostExecute(resultado: List<Estatisticas>?) {
            super.onPostExecute(resultado)
            atualizarEstatisticas(resultado)
        }
    }
}
