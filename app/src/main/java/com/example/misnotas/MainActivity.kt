package com.example.misnotas

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nota_layout.view.*
import java.io.*
import java.lang.Exception


class MainActivity : AppCompatActivity() {
    var notas =ArrayList<Nota>()
    lateinit var adaptador:AdaptadorNotas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener{
            var intent = Intent(this, AgregarNotaActivity::class.java)
            startActivityForResult(intent,123)
        }
        leerNotas()

        adaptador = AdaptadorNotas(this,notas)
        listita.adapter = adaptador
    }

    fun leerNotas(){

        notas.clear()
        var carpeta = File(ubicacion().absolutePath)
        if (carpeta.exists()){
            var archivos = carpeta.listFiles()
            if(archivos != null){
                for (archivo in archivos){
                    leerArchivo(archivo)
                }
            }
        }
    }

    fun leerArchivo(archivo:File){
        val fis = FileInputStream(archivo)
        val di = DataInputStream(fis)
        val br = BufferedReader(InputStreamReader(di))
        var strLine:String? = br.readLine()
        var myData = ""

        while (strLine != null){
            myData = myData + strLine
            strLine = br.readLine()
        }
        br.close()
        di.close()
        fis.close()

        var nombre = archivo.name.substring(0, archivo.name.length-4)
        var nota = Nota(nombre,myData)
        notas.add(nota)
    }

    private fun ubicacion(): File{
        val folder =File(getExternalFilesDir(null), "")
        if(!folder.exists()){
            folder.mkdir()
        }
        return folder
    }


    class AdaptadorNotas:BaseAdapter{
        var context:Context
        var notas=ArrayList<Nota>()

        constructor(context: Context, notas:ArrayList<Nota>){
            this.context = context
            this.notas = notas
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            var inflador = LayoutInflater.from(context)
            var vista = inflador.inflate(R.layout.nota_layout, null)
            var nota = notas[p0]

            vista.tv_titulo_det.text = nota.titulo
            vista.tv_contenido_det.text = nota.contenido

            vista.btn_borrar.setOnClickListener(){
                eliminar(nota.titulo)
                notas.remove(nota)
                this.notifyDataSetChanged()
            }

            return vista
        }

        private fun eliminar(titulo:String){
            if(titulo == ""){
                Toast.makeText(context,"Error: titulo vacío", Toast.LENGTH_SHORT).show()
            }else{
                try {
                    val archivo = File(ubicacion(), titulo + ".txt")
                    archivo.delete()
                    Toast.makeText(context, "Se elimino el archivo", Toast.LENGTH_SHORT).show()
                }catch (e:Exception){
                    Toast.makeText(context, "Error al eliminar el archivo", Toast.LENGTH_SHORT).show()
                }
            }
        }

        private fun ubicacion():String{
            val album = File(context?.getExternalFilesDir(null),"notas")
            if (!album.exists()){
                album.mkdir()
            }
            return album.absolutePath
        }


        override fun getItem(p0: Int): Any {
            return notas[p0]
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getCount(): Int {
            return notas.size
        }

    }

}

