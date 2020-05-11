package com.ap8.api_covid

import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_mundo_paises.*
import java.text.DecimalFormat

class MundoActivity : AppCompatActivity() {

    private var estatisticasList = mutableListOf<Estatisticas>()
    private var asyncTask: EstatisticasTask? = null
    val acumulador = Estatisticas(
        country = null,
        uf = null,
        state = null,
        suspects = 0,
        refuses = 0,
        cases = 0,
        confirmed = 0,
        deaths = 0,
        recovered = 0,
        date = null,
        hour = null
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mundo_paises)

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

    inner class EstatisticasTask: AsyncTask<Void, Void, List<Estatisticas>>() {
        override fun doInBackground(vararg params: Void?): List<Estatisticas>? {
            val path = "/countries"
            return EstatisticasHTTP.loadEstatisticas(path)
        }
        override fun onPostExecute(resultado: List<Estatisticas>?) {
            super.onPostExecute(resultado)
            atualizarEstatisticas(resultado)
        }
    }

    private fun atualizarEstatisticas(resultado: List<Estatisticas>?) {
        if(resultado != null) {
            this.estatisticasList.clear()
            this.estatisticasList.addAll(resultado)
            acumularDados()
            exibirDados()
        }
    }

    fun acumularDados() {
        for(index in 0 .. estatisticasList.size - 1) {
            val pais = estatisticasList[index]
            acumulador.cases = acumulador.cases + pais.cases
            acumulador.confirmed = acumulador.confirmed + pais.confirmed
            acumulador.deaths = acumulador.deaths + pais.deaths
            acumulador.recovered = acumulador.recovered + pais.recovered
        }
    }

    fun exibirDados() {
        val df = DecimalFormat("###,###")
        view_casesResult.text = df.format(acumulador.cases)
        view_confirmedResult.text = df.format(acumulador.confirmed)
        view_recoveredResult.text = df.format(acumulador.recovered)
        view_deathsResult.text = df.format(acumulador.deaths)
        textView_title.text = "Estatísticas \n Mundiais:"
    }
}
