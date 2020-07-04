package wav.lib;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

import org.jtransforms.fft.*;
import org.jtransforms.fft.DoubleFFT_1D;
import org.jtransforms.utils.IOUtils;




public class readWav {
	
	//Archivo
	//private final static String sampleDir = "C:\\Users\\Álvaro De La Torre\\Desktop\\TFG\\record.wav";
	private final static String sampleDir = iu.SFile.dir_x+"\\record.wav";
	private static final int FREQ = 48000; //Frequency

	//ArrayList para output
	public static ArrayList<String> output = new ArrayList<String>();
	
	static String c = iu.Inicio.accuracy_x;
	private static final int CUT = Integer.parseInt(c); //Arrays cut frequency
	
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_RESET = "\u001B[0m";
	
	//Output
	//public static String [] res;
	
	   public static void read()
	   {
		  

		   
		  final File file = new File(sampleDir);
	      try
	      {
	         // Open the wav file specified as the first argument
	         WavFile wavFile = WavFile.openWavFile(file);
	         
	         // Get number of frames to read (Full Forward x2 R e I) (Cast to int from float, could be wrong, we will see)
	         int n = (int) wavFile.getNumFrames();

	         // Display information about the wav file
	         wavFile.display();

	         // Get the number of audio channels in the wav file
	         int numChannels = wavFile.getNumChannels();

	         // Create a buffer of 1000 frames
	         double[] buffer = new double[n * numChannels];

	         int framesRead;
	         double min = Double.MAX_VALUE;
	         double max = Double.MIN_VALUE;

	         do
	         {
	            // Read frames into buffer
	            framesRead = wavFile.readFrames(buffer, n);

	            // Loop through frames and look for minimum and maximum value
	            for (int s=0 ; s<framesRead * numChannels ; s++)
	            {
	               if (buffer[s] > max) max = buffer[s];
	               if (buffer[s] < min) min = buffer[s];
	            }
	         }
	         while (framesRead != 0);

	         // Close the wavFile
	         wavFile.close();

	         // Output the minimum and maximum value
	         System.out.printf("Min: %f, Max: %f\n", min, max);

	         
	         
	         //Fragments************************************************************
	         
	         //N = numero de miniBuffers que se crean sin funcion techo
	         double aN = buffer.length/CUT;
	         //Funcion techo a N para coger todos los valores
	         int N = (int) Math.ceil(aN);
	         
	         //Array con el conjunto de notas
	         String[] conjunto = new String[N];
	         
	         //Recorre array buffer pequenos N veces
	         for (int f = 0; f < N; f++){
	        
	        	 int ruido = 0;
	        	 //Inicializamos y rellenamos los buffers antes de aplicar fft (dom(t))
	        	 double[] miniBuffer = new double[CUT];
	        	 for (int m=0; m<CUT; m++) {
	        		 miniBuffer [m] =  buffer [(f*CUT)+m];
	        		 
	        		 //Ventana de Hann sample*(1 - cos(2*PI*i/Sample_size))/2
	        		 miniBuffer [m] = miniBuffer [m] * 0.5 * (1.0 - Math.cos(2.0*Math.PI*m/CUT));
	        		 
	        		 
	        	 }
	        	 
	        	 
	        	 
	        	 DoubleFFT_1D ff = new DoubleFFT_1D(CUT);
	        	 
	        	 //Se hace FFT a un miniBuffer
	        	 ff.realForward(miniBuffer);
	        	 
	        	 //Eliminar componentes fuera de la ventana de frecuencia (C1 to A4) que es el rango del bajo en E Standard
	        	 double hz = ((double)( miniBuffer.length * 2)) /FREQ;
        		 for(int z = 0; z>(7040*hz); z++) {
        			 miniBuffer[z] = 0;
        		 }
        		 for(int z = 0; z<(39*hz); z++) {
        			 miniBuffer[z] = 0;
        		 }
	 


	        	 //Sacar pico de frecuencia
	        	 double m_max = miniBuffer[0];
	        	 int i_max = 0;
	        	 for (int z = 1; z<CUT; z++) {
	        		 
	        		 if (miniBuffer[z] > m_max) {
	        			 m_max = miniBuffer[z];
	        			 i_max = z;
	        		 }
	    	         
	        	 }
	        	 
	    	     double f_max;
	    	     f_max = (i_max * FREQ / CUT)/2; //Factor 2
	    	     

	    	     //Elimiar sonido sin suficiente dB (microfono al 60%, bajo al 100%)
	    	     if(m_max < 80) {
	    	    	 System.out.println("RUIDO");
	    	    	 ruido = 1;
	    	     }
	    	     else {
	    	    	 System.out.print("Pico de frec: ");
	    	    	 System.out.println(f_max);
	    	    	 ruido = 0;
	    	     }
	    	     
	    	     if(ruido == 0) {
	    	    	 
	    	     
	    	     //HPS  
	    	    	 
        		 int i_max_h = 0;
        		 double m_max_h = miniBuffer[0];
        		 m_max_h = m_max;
        		 double prod [] = new double[miniBuffer.length];
        		 double fund_freq = 0.0;
        		 
        		 for(int k = 0; k < CUT/5 ; k++)
        		 	{
        			 
        		     prod[k] = ((miniBuffer[k]*24000) * (miniBuffer[2*k]*24000) * (miniBuffer[3*k]*24000) * (miniBuffer[4*k]*24000) * (miniBuffer[5*k]*24000));//Multiplico 24000 para evitar la reducción de los valores por normalización (-1 to 1)

        		     // find fundamental frequency (maximum value in plot)
        		     if( prod[k] > m_max_h && k > 0 )
        		     	{
        		         	m_max_h = prod[k];
        		         	i_max_h = k;
        		     	}
        		 	}
        		 	
        		 
        		  	fund_freq = (i_max_h * FREQ / CUT)/2;
        		  	System.out.print("Frecuencia fundamental: ");
        		  	System.out.println(fund_freq);
        		  	
        		  	
        		  	//Convertir a nota la frecuencia
        		  	String nota;
        		  	nota = FrequencyToNote(fund_freq, CUT);
        		  	
        		  	//Introducir en array las notas
        		  	conjunto [f] = nota;
        		  	System.out.print("Nota: ");
        		  	System.out.println(nota);
        		  	System.out.println("---------------------------------------------------------------------------------------------------------------------------------");

	    	     }
	    	     
	    	     else {
	    	    	conjunto [f] = "0RU";
	    	    	System.out.println("---------------------------------------------------------------------------------------------------------------------------------");
	    	     }

	        }
	      
	        
	      //Ajustar el conjunto de notas para el output   
	      String [] music = Ajuste(conjunto, N);

	      //Introducimos el output final en el ArrayList
	      Collections.addAll(output, music);
	      
	      for(int r = 0; r < N; r++) {
	    	  System.out.println(music[r]);
	    	  //res [r] = music [r];
	      } 
	         
	      }
	      catch (Exception e)
	      {
	         System.err.println(e);
	      }
	   }
	   

	   public static String FrequencyToNote(double fund_freq, int CUT) {
		   
		   String nota = "";
		  
		  if(CUT == 12000) {
			  
			  if (fund_freq<39) {
				   //RUIDO
				   nota = "LOE";
			   }
			   else if (fund_freq>39 && fund_freq<=42) {
				   //E1
				   nota = "1E-";
			   }
			   else if (fund_freq>42 && fund_freq<=44) {
				   //F1
				   nota = "1F-";
			   }
			   else if (fund_freq>44 && fund_freq<=46) {
				   //F1#
				   nota = "1F#";
			   }
			   else if (fund_freq>46 && fund_freq<=50) {
				   //G1
				   nota = "1G-";
			   }
			   else if (fund_freq>50 && fund_freq<=52) {
				   //G1#
				   nota = "1G#";
			   }
			   else if (fund_freq>52 && fund_freq<=56) {
				   //A1
				   nota = "1A-";
			   }
			   else if (fund_freq>56 && fund_freq<=60) {
				   //A1#
				   nota = "1A#";
			   }
			   else if (fund_freq>60 && fund_freq<=62) {
				   //B1
				   nota = "1B-";
			   }
			   else if (fund_freq>62 && fund_freq<=66) {
				   //C2
				   nota = "2C-";
			   }
			   else if (fund_freq>66 && fund_freq<=70) {
				   //C2#
				   nota = "2C#";
			   }
			   else if (fund_freq>70 && fund_freq<=74) {
				   //D2
				   nota = "2D-";
			   }
			   else if (fund_freq>74 && fund_freq<=80) {
				   //D2#
				   nota = "2D#";
			   }
			   else if (fund_freq>80 && fund_freq<=84) {
				   //E2
				   nota = "2E-";
			   }
			   else if (fund_freq>84 && fund_freq<=88) {
				   //F2
				   nota = "2F-";
			   }
			   else if (fund_freq>88 && fund_freq<=94) {
				   //F2#
				   nota = "2F#";
			   }
			   else if (fund_freq>94 && fund_freq<=100) {
				   //G2
				   nota = "2G-";
			   }
			   else if (fund_freq>100 && fund_freq<=106) {
				   //G2#
				   nota = "2G#";
			   }
			   else if (fund_freq>106 && fund_freq<=112) {
				   //A2
				   nota = "2A-";
			   }
			   else if (fund_freq>112 && fund_freq<=118) {
				   //A2#
				   nota = "2A#";
			   }
			   else if (fund_freq>118 && fund_freq<=126) {
				   //B2
				   nota = "2B-";
			   }
			   else if (fund_freq>126 && fund_freq<=134) {
				   //C3
				   nota = "3C-";
			   }
			   else if (fund_freq>134 && fund_freq<=142) {
				   //C3#
				   nota = "3C#";
			   }
			   else if (fund_freq>142 && fund_freq<=150) {
				   //D3
				   nota = "3D-";
			   }
			   else if (fund_freq>150 && fund_freq<=158) {
				   //D3#
				   nota = "3D#";
			   }
			   else if (fund_freq>158 && fund_freq<=170) {
				   //E3
				   nota = "3E-";
			   }
			   else if (fund_freq>170 && fund_freq<=180) {
				   //F3
				   nota = "3F-";
			   }
			   else if (fund_freq>180 && fund_freq<=190) {
				   //F3#
				   nota = "3F#";
			   }
			   else if (fund_freq>190 && fund_freq<=200) {
				   //G3
				   nota = "3G-";
			   }
			   else if (fund_freq>200 && fund_freq<=212) {
				   //G3#
				   nota = "3G#";
			   }
			   else if (fund_freq>212 && fund_freq<=226) {
				   //A3
				   nota = "3A-";
			   }
			   else if (fund_freq>226 && fund_freq<=240) {
				   //A3#
				   nota = "3A#";
			   }
			   else if (fund_freq>240 && fund_freq<=254) {
				   //B3
				   nota = "3B-";
			   }
			   else if (fund_freq>254 && fund_freq<=268) {
				   //C4
				   nota = "4C-";
			   }
			   else if (fund_freq>268 && fund_freq<=286) {
				   //C4#
				   nota = "4C#";
			   }
			   else if (fund_freq>286 && fund_freq<=302) {
				   //D4
				   nota = "4D-";
			   }
			   else if (fund_freq>302 && fund_freq<=320) {
				   //D4#
				   nota = "4D#";
			   }
			   else if (fund_freq>320 && fund_freq<=340) {
				   //E4
				   nota = "4E-";
			   }
			   else if (fund_freq>340 && fund_freq<=360) {
				   //F4
				   nota = "4F-";
			   }
			   else if (fund_freq>360 && fund_freq<=382) {
				   //F4#
				   nota = "4F#";
			   }
			   else if (fund_freq>382 && fund_freq<=404) {
				   //G4
				   nota = "4G-";
			   }
			   else if (fund_freq>404) {
				   //RUIDO
				   nota = "HIE";
			   }
		  }
		  
		  //24000 Accuracy
		  else {
			  if (fund_freq<39) {
				   //RUIDO
				   nota = "LOE";
			   }
			   else if (fund_freq>39 && fund_freq<=42) {
				   //E1
				   nota = "1E-";
			   }
			   else if (fund_freq>42 && fund_freq<=45) {
				   //F1
				   nota = "1F-";
			   }
			   else if (fund_freq>45 && fund_freq<=48) {
				   //F1#
				   nota = "1F#";
			   }
			   else if (fund_freq>48 && fund_freq<=50) {
				   //G1
				   nota = "1G-";
			   }
			   else if (fund_freq>50 && fund_freq<=53) {
				   //G1#
				   nota = "1G#";
			   }
			   else if (fund_freq>53 && fund_freq<=56) {
				   //A1
				   nota = "1A-";
			   }
			   else if (fund_freq>56 && fund_freq<=60) {
				   //A1#
				   nota = "1A#";
			   }
			   else if (fund_freq>60 && fund_freq<=63) {
				   //B1
				   nota = "1B-";
			   }
			   else if (fund_freq>63 && fund_freq<=67) {
				   //C2
				   nota = "2C-";
			   }
			   else if (fund_freq>67 && fund_freq<=70) {
				   //C2#
				   nota = "2C#";
			   }
			   else if (fund_freq>70 && fund_freq<=75) {
				   //D2
				   nota = "2D-";
			   }
			   else if (fund_freq>75 && fund_freq<=80) {
				   //D2#
				   nota = "2D#";
			   }
			   else if (fund_freq>80 && fund_freq<=85) {
				   //E2
				   nota = "2E-";
			   }
			   else if (fund_freq>85 && fund_freq<=90) {
				   //F2
				   nota = "2F-";
			   }
			   else if (fund_freq>90 && fund_freq<=95) {
				   //F2#
				   nota = "2F#";
			   }
			   else if (fund_freq>95 && fund_freq<=101) {
				   //G2
				   nota = "2G-";
			   }
			   else if (fund_freq>101 && fund_freq<=107) {
				   //G2#
				   nota = "2G#";
			   }
			   else if (fund_freq>107 && fund_freq<=113) {
				   //A2
				   nota = "2A-";
			   }
			   else if (fund_freq>113 && fund_freq<=120) {
				   //A2#
				   nota = "2A#";
			   }
			   else if (fund_freq>120 && fund_freq<=127) {
				   //B2
				   nota = "2B-";
			   }
			   else if (fund_freq>127 && fund_freq<=135) {
				   //C3
				   nota = "3C-";
			   }
			   else if (fund_freq>135 && fund_freq<=143) {
				   //C3#
				   nota = "3C#";
			   }
			   else if (fund_freq>143 && fund_freq<=151) {
				   //D3
				   nota = "3D-";
			   }
			   else if (fund_freq>151 && fund_freq<=160) {
				   //D3#
				   nota = "3D#";
			   }
			   else if (fund_freq>160 && fund_freq<=170) {
				   //E3
				   nota = "3E-";
			   }
			   else if (fund_freq>170 && fund_freq<=180) {
				   //F3
				   nota = "3F-";
			   }
			   else if (fund_freq>180 && fund_freq<=190) {
				   //F3#
				   nota = "3F#";
			   }
			   else if (fund_freq>190 && fund_freq<=201) {
				   //G3
				   nota = "3G-";
			   }
			   else if (fund_freq>201 && fund_freq<=213) {
				   //G3#
				   nota = "3G#";
			   }
			   else if (fund_freq>213 && fund_freq<=226) {
				   //A3
				   nota = "3A-";
			   }
			   else if (fund_freq>226 && fund_freq<=240) {
				   //A3#
				   nota = "3A#";
			   }
			   else if (fund_freq>240 && fund_freq<=254) {
				   //B3
				   nota = "3B-";
			   }
			   else if (fund_freq>254 && fund_freq<=269) {
				   //C4
				   nota = "4C-";
			   }
			   else if (fund_freq>269 && fund_freq<=285) {
				   //C4#
				   nota = "4C#";
			   }
			   else if (fund_freq>285 && fund_freq<=302) {
				   //D4
				   nota = "4D-";
			   }
			   else if (fund_freq>302 && fund_freq<=320) {
				   //D4#
				   nota = "4D#";
			   }
			   else if (fund_freq>320 && fund_freq<=339) {
				   //E4
				   nota = "4E-";
			   }
			   else if (fund_freq>339 && fund_freq<=359) {
				   //F4
				   nota = "4F-";
			   }
			   else if (fund_freq>359 && fund_freq<=381) {
				   //F4#
				   nota = "4F#";
			   }
			   else if (fund_freq>381 && fund_freq<=404) {
				   //G4
				   nota = "4G-";
			   }
			   else if (fund_freq>404) {
				   //RUIDO
				   nota = "HIE";
			   }
		  }
		   
		   
		   return nota;
		   
	   }
	   
	   
	   public static String [] Ajuste(String [] conjunto, int N) {

		   
		   
		   //String array para output
		   String [] music = new String[N];
		   //String array para nota
		   String [] note = new String[N];
		   //Int array para octava
		   int [] octave = new int [N];
		   //Repeticiones de notas iguales sin incluir la primera
		   //int rep = 0;
		   //Int array para max octava
		   int[] oct = new int [N];
		   int max_oct = 0;
		   int first_ind = 0;
		   
		   //Rellenar Arrays de note y octave
		   for(int index = 0; index < N; index++) {
			   note [index] = conjunto[index].substring(1, 3);
			   octave [index] = Integer.parseInt(conjunto[index].substring(0, 1));
			   System.out.println(note [index] + " | " + octave [index]);
		   }
		   
		   System.out.println("---------------------------------------------------------------------------------------------------------------------------------");
		   
		   int index = 0;
		   while(index < N) {
			   if (note[index].equals("RU")) {
				   music [index] = " -- ";
				   index ++;
			   }
			   else {
				   music [index] = note [index];
				   first_ind=index;
				   max_oct = octave [index];
				   index++;
				   if(index<N) {
					   //rep=0;
					   while ((index)<N && note[index-1].equals(note[index]) && !(note[index].equals("RU"))) {
						   //rep = rep+1;
						   music [index] = " [] ";
						   if(octave[index]<max_oct) {
							   max_oct=octave[index];
						   }
						   index ++;
					   }
					   octave[first_ind] = max_oct;
					   music [first_ind] = note [first_ind] + Integer.toString(octave[first_ind]);
				   }

			   }
		   }
		   
		   for(int inde = 0; inde < N; inde++) {

			   System.out.println(note [inde] + " | " + octave [inde]);
		   }
		   

		   return music;	   
				   
		
		   
	   }
	   
	   

	}

