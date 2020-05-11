package com.ap8.api_covid

import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_mundo_paises.*
import java.text.DecimalFormat

class DadosActivity : AppCompatActivity() {

    private var asyncTask: EstatisticasTask? = null
    var paisOuEstado = "null"
    var endereco = ""
    private var dados_ = Estatisticas(
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
    private var dados = dados_

    override fun onCreate(savedInstanceState: Bundle?) {
        val name_ = intent.getStringExtra("nome")
        val endereco_ = intent.getStringExtra("endereco")
        paisOuEstado = name_
        endereco = endereco_
        if(endereco == "estados") {
            setContentView(R.layout.activity_estado_result)
        } else if(endereco == "paises") {
            setContentView(R.layout.activity_mundo_paises)
        }
        super.onCreate(savedInstanceState)

        carregarEstatisticas()
    }

    private fun carregarEstatisticas() {
        dados = dados_
        if(asyncTask == null) {
            if(EstatisticasHTTP.hasConnection(this)) {
                if(asyncTask?.status != AsyncTask.Status.RUNNING) {
                    asyncTask = EstatisticasTask()
                    asyncTask?.execute()
                }
            }
        }
    }

    inner class EstatisticasTask: AsyncTask<Void, Void, Estatisticas>() {
        override fun doInBackground(vararg params: Void?): Estatisticas? {
            var path = ""
            if(endereco == "estados") {
                path = "/${paisOuEstado}"
            } else if(endereco == "paises") {
                path = "/${paisOuEstado}"
            }

            val list = EstatisticasHTTP.loadEstatisticas(path)
            return list?.get(0)
        }
        override fun onPostExecute(resultado: Estatisticas?) {
            super.onPostExecute(resultado)
            atualizarEstatisticas(resultado)
        }
    }

    private fun atualizarEstatisticas(resultado: Estatisticas?) {
        if(resultado != null) {
            dados = dados_
            dados = resultado
        }
        exibirDadosPais()
    }

    fun exibirDadosPais() {
        val df = DecimalFormat("###,###")
        view_casesResult.text = df.format(dados.cases)
        view_confirmedResult.text = df.format(dados.confirmed)
        view_recoveredResult.text = df.format(dados.recovered)
        view_deathsResult.text = df.format(dados.deaths)
        textView_title.text = "${paisOuEstado} \n Estatísticas:"
    }
}
