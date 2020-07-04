package ftt.trans;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.io.*;
import java.util.Vector;
import javax.sound.sampled.*;
import javax.sound.midi.*;


public class Record{

	static final long TIME_RECORDING = iu.Inicio.time_x;  //Se obtiene valor de IU
	
	//Path para almacenar el archivo en .WAV
	File wavFile = new File(iu.SFile.dir_x+"\\record.wav");
	
	
	//Formato del archivo de audio (tipo wave)
    AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
	
    //Inicializamos un DataTarget para almacenar informacion
    TargetDataLine line;
    
    

    //Definicion del formato del audio a capturar
    AudioFormat getAudioFormat() {
        float sampleRate = 48000;
        int sampleSize = 16;
        int channels = 1;
        boolean endian = true; //Big Endian
        boolean signed = true;
        AudioFormat formato = new AudioFormat(sampleRate, sampleSize,
                                             channels, signed, endian);
        return formato;
    }
    

    //Captura del audio
    
    void start() {
        try {
            AudioFormat format = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
 
            //Comprobacion de soporte
            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Line not supported");
                System.exit(0);
            }
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            
            //Captura de la linea
            line.start();
            AudioInputStream inputStream = new AudioInputStream(line);
 
            System.out.println("Recording...");
 
            //Escritura en el archivo .wav
            AudioSystem.write(inputStream, fileType, wavFile);
            
            //Errores
        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
 
    
     //Cierra el DataLine, la captura y la grabacion
    
    void finish() {
        line.stop();
        line.close();
        System.out.println("End of record");
    }
    
    
    //MAIN
    
	public static void main(String[] args) {
			
		 final Record recorder = new Record();
		 
	        //Crea un thread para grabar y espera el tiempo especificado de grabacion
		 
	        Thread stopper = new Thread(new Runnable() {
	            public void run() {
	                try {
	                	//Espera TIME_RECORDING sec
	                    Thread.sleep(TIME_RECORDING);
	                } catch (InterruptedException ex) {
	                    ex.printStackTrace();
	                }
	                recorder.finish();
	            }
	        });
	 
	        //Parar el thread si vamos a empezar uno nuevo
	        stopper.start();
	 
	        //Funcion Grabacion
	        recorder.start();
	        
	        //Hacer arrays de la grabacion
	        wav.lib.readWav.read();
	        
	        
	      
	    }
	
	public static void mainCaller() 
    {
		Record.main(null);
    }
		
}
