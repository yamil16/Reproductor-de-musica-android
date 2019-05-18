# Reproductor de musica android


Introducción: 


	Se trata de una aplicación de reproducción de música para Androide que permite: 

	Seleccionar y reproducir un álbum de los presentes en el dispositivo. 

	Crear playlist personalizadas. 

	Control básico de la reproducción desde la notificación. 

	Widget con control básico de la reproducción. 

	Obtener las letras de las canciones desde un sitio Web. 

	Permite seguir reproduciendo en segundo plano. 


Desarrollo: 


En primer lugar, se diseñó el logo de la aplicación por medio de una página de diseño de iconos, se 
obtuvo un diseño llamativo y juvenil relacionándolo con la música a través de los auriculares. El 
nombre de la aplicación se ideó a partir del mismo logo.  
Al utilizar la aplicación, se carga por defecto todas las playlist del dispositivo  y se puede visualizar 
fácilmente todos los álbumes como así también se puede crear una nueva lista de reproducción. 
Buscar los álbumes o las playlist es posible realizando una consulta a la base de datos de Android, 
mediante el siguiente comando: 
context.getContentResolver().query(uri,columnas,selección,null,orden), donde la uri es donde está 
la tabla de la BD, columnas son las columnas que se retornan por cada fila, selección especifica el 
criterio de selección para cada fila y orden especifica el orden que aparecen las filas en el cursor. 
 Esa consulta debe ser adaptada al ListView, y se las adapta de manera diferente si es un álbum o 
una playlist. 
Si se desea crear una nueva lista de reproducción, se abre un AlertDialog con el propósito de escribir 
el nombre de la nueva lista de reproducción. 

En caso de aceptar, se seleccionan las canciones que se desean añadir a la lista de reproducción, 
presionando sobre las diferentes canciones que se observan en el ListView.
Se seleccionan las canciones para la lista de reproducción Playlist Personalizada.  
Si se desea dejar de agregar canciones, se retorna  con la flecha hacia atrás que nos lleva a la ventana 
inicial, donde podemos seleccionar la lista creada para reproducirla.  
Una vez seleccionada la playlist se puede observar la reproducción de la misma.
Si se dispone de internet, se puede acceder a ver la letra de la canción desde una página web que 
contiene una amplia base de datos de letras de canciones. 
Para este ejemplo la consulta se realizó de la siguiente manera: 
http://lyrics.wikia.com/wiki/Los_Cafres:Bastará 

En caso de que el artista sea “unknown”, se pide que el nombre de la canción se componga del 
Artista y después del nombre de la canción, para poder realizar el matching en la base de datos.  
Para nuestro ejemplo la canción en nuestro móvil fue guardada de la siguiente forma: Los Cafres-
Bastará.mp3 

Por último,  podemos observar cómo se ejecuta el reproductor de música en segundo plano y como 
se puede controlar la reproducción mediante la barra de notificaciones con los botones Previus, 
Play/Pause y Next.  

Resumen de las clases: 

SeleccionMusicaPlayList: Es la clase que se encarga de mostrar todas las canciones del dispositivo 
y de agregar las canciones a la playlist creada. 

MainActivity: Es la clase principal, que se corresponde con el menú inicial de la aplicación donde se 
muestran todas las playlist, álbumes, se crean nuevas playlist. 

AdaptarListViewMusica: Esta clase se utiliza para adaptarse al listView y poder mostrar todos los 
álbumes del dispositivo. 

AdaptarListViewPlaylist: Es similar a AdaptarListViewMusica, pero con la diferencia que muestra 
todas las playlist del sistema. 

WidgetReproductorMusica: Su principal función es crear el widget y actualizar el sistema ante un 
evento del widget, como por ejemplo presionar el botón next del widget y reproducir la siguiente 
canción. 

Cancion: Esta clase guarda todos los atributos principales que tiene una canción. 

PlayList: Tiene todos los atributos para definir una playlist. 

LocalServiceConnector: Es una clase privada de la clase ReproducirCanciones y se encarga de 
enlazar la actividad con el ServicioMusica, para poder enviar solicitudes, recibir respuesta e incluso 
establecer comunicación.  

ReproducirCanciones: Esta clase se centra en la ventana de reproducir canciones, donde podemos 
controlar la reproducción de una canción mediante una comunicación con el ServicioMusica, cargar 
la letra de las canciones y volver al menú inicial. 

ServicioMusica: Esta clase responde las solicitudes que llegan, cambiando la canción, pausando la 
reproducción, reanudando la reproducción, etc. También crea la barra de notificaciones, carga las 
canciones a reproducir. 

ReproductorBinder: Es una clase privada del ServicioMusica, que se encarga de devolver el servicio. 