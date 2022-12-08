package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.ui.Picture;

public class Main extends SimpleApplication 
{
    // creamos las variables que tendran las teclas del raton y del teclado para interactiar.
    /**
     * TRIGGER_KILL se encarga de tomar la entrada del raton para efectuar los disparos.
     * el boton encargado de esya aaccion es buton left.
     */
    private final static Trigger TRIGGER_KILL = new MouseButtonTrigger(MouseInput.BUTTON_LEFT);  
    /**
     * Para reinicar la partida se tomo el teclado con la tecla 'R' la cual es guardada en TRIGGER_RESET 
     */
    private final static Trigger TRIGER_RESET = new  KeyTrigger(KeyInput.KEY_R);
    private final static String MAPPING_KILL = "KILL";
    private final static String MAPPIN_DATOS =  "DATOS";
    private final static String MAPPIN_RESET = "REINCIAR";
    // nodo donde los enemigos seran agregados.
    private Node nodeR =  new Node("Nodo_enemogos");
    //hasemos instancia a la clase 'Picture' para poder crear las pantallas de ganaste o perdiste 
    Picture pic = new Picture("HUD Picture");
    int vida=3; // variable de la vida que tendra la puerta
    float x =2; // 
    // creamos una variable de tipo BitmapText para el hud en pantalla.
    public BitmapText hudText;
    // con esto vamos a contar los enemigos moridos.
    int contadorEnemigosMoridos = 0;
    /**
     * geometria que se usa de modo temporal obtener los datos de la caja que se selecciono 
     * es decir el enemigo.
     **/
    private Geometry box01_geom;
    // variable de tipo flotante que cuenta la distancia que ha recorrido. 
    float mov = 0; 
    /**
     * Variable de tipo BOX que funciona como plantilla para los enemigos, 
     * es decir esta proporciona el tamaño y ubicacion de los enemigos nuevos.
     */
    public static Box mesh = new Box(Vector3f.ZERO, 1,1,1);
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp()
    {
         /**
          * se agrega en el inputManager los mapas de las entradas de teclado y raton para
          * usarlas en addListener
          **/
        inputManager.addMapping(MAPPING_KILL,TRIGGER_KILL);
        inputManager.addListener(analogListener, new String[]{MAPPING_KILL});
        //inputManager.addListener(actionListener, new String[]{MAPPIN_DATOS});
        inputManager.addMapping(MAPPIN_RESET,TRIGER_RESET);
        inputManager.addListener(analogListener, new String[]{MAPPIN_RESET});
        // hudText inicialisa el texto del hud.
        hudText = new BitmapText(guiFont, true);
        nodeR.move(0,0,0); // movemos la posiscion del nodeR a la 0,0,0.
        // ponemos la posicion de la camara cerca de la puerta.
        cam.setLocation(new Vector3f(-10,9,95));
        // creamos los objetos del escenario.
        Box suelo = new Box(15,.05f,100);
        Box pared = new Box(15,5,0);
        
        Geometry geomSuelo = new Geometry("Box",suelo);
        Geometry paredGeom = new Geometry("Box",pared);
        
        Material matPared =  new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        matPared.setTexture("ColorMap",assetManager.loadTexture("Textures/puerta.jpeg"));
        paredGeom.setMaterial(matPared);
        
        Material mat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap",assetManager.loadTexture("Textures/piso.jpg"));
        geomSuelo.setMaterial(mat);
        
        atachCenterMark(); //  es la marca que nos servira como referencia para saber a que le estamos pegando.
        hudPuntos();// llamamos a la metodo que muestra el HUD  en pantalla.
        paredGeom.move(0,3,100);
        aparecerEnemigos();// llamamos al metodo que genera enemigos.
        rootNode.attachChild(paredGeom);
        rootNode.attachChild(geomSuelo);
    }// fin del simple inti app 
    
     private final AnalogListener analogListener = new AnalogListener(){
        @Override
        public void onAnalog(String name, float intensity, float tpf) {
            //si el enombre de la tecla es REINCIAR se tomara como si hubiera precionado la tecla R
            // y reinicara el juego.
            if (name.equals(MAPPIN_RESET))
            {
                reiniciar();
            }
            // si el boton precionado es el click del raton significa que seleccionar algo con el rayo.
            if(name.equals(MAPPING_KILL))
            {
                // creamos una variable de tipo coliccion para guardar los datos del rayo.
                CollisionResults results = new CollisionResults();
                // a la camara se le crea el rayo para poder ver que ha seleccionado.
                Ray ray = new Ray(cam.getLocation(), cam.getDirection());
                rootNode.collideWith(ray, results);
                // si el tamaño de results es mayor a 0 quiere decir que ha colicionado con algun objeto.
                if(results.size()>0)
                {
                    // guardamos en la geometria el nombre del objeto y sus coliciones
                    Geometry target = results.getClosestCollision().getGeometry();
                    // comprobamos el nombre del objeto y si es uno de los que se encuentran abajo es un enemigo
                    // y lo destruye, aumenta el contador contadorEnemigosMoridos y aparece mas enemigos.
                    switch(target.getName())
                    {
                        case "Red_cube":
                        {
                            nodeR.detachChild(target);
                            contadorEnemigosMoridos++;
                            aparecerEnemigos();
                            break;
                        }// fin del primer caso Red_cube.
                        case "Yellow_cube":{
                            nodeR.detachChild(target);
                            contadorEnemigosMoridos++;
                            aparecerEnemigos();
                            break;
                        }
                        
                        case "Green_cube":
                        {
                            nodeR.detachChild(target);
                            contadorEnemigosMoridos++;
                            aparecerEnemigos();
                            break;
                        }// fin del primer caso Red_cube.
                        case "Blue_cube":{
                            nodeR.detachChild(target);
                            contadorEnemigosMoridos++;
                            aparecerEnemigos();
                            break;
                        }
                    }// fin del swich.
                }
            }
        }   
    };
     /*
     private final ActionListener actionListener = new ActionListener(){
        @Override
        public void onAction(String name, boolean isPressed, float tpf) 
        {
            // asigna colores aleatrorios a cada enemigo.
            System.out.println("you triggered : "+name);
            if(name.equals(MAPPIN_DATOS) && !isPressed){
                box01_geom.getMaterial().setColor("Color", ColorRGBA.randomColor());
            }
        }    
    }; */
     
     /**
      * @param name recive el nombre del enemio.
      * @param loc recive la localizacion. 
      * @param color recive el color.
      * @return geom retorna la geometia.
      */
    private Geometry enemigos(String name, Vector3f loc, ColorRGBA color)
    {
        
        Geometry geom = new Geometry(name,mesh);
        Material mat = new Material(assetManager , "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        geom.setMaterial(mat);
        geom.setLocalTranslation(loc);
        return geom;
    }
    
    private void atachCenterMark()
    {
        // crea la marca que nos india a donde estamos a puntando.
        Geometry c = this.enemigos("center mark", Vector3f.ZERO, ColorRGBA.White);
        c.scale(4);
        c.setLocalTranslation(settings.getWidth()/2, settings.getHeight()/2, 0);
        guiNode.attachChild(c);
    }
 
    @Override
    public void simpleUpdate(float tpf) 
    {
        // para que camine en zic zac.
        if(mov<100 && x <= 2 && x>0 && mov>-1)
        {
           
            nodeR.move(x-=0.5f,0,tpf*10);
            mov= mov + tpf*10;
            if(x == 0)
            {
                x = -2;
            }
        }
        else if(x>=-2&& x<0)
        {
            nodeR.move(x+=0.5f,0,tpf*10);
            mov= mov + tpf*10;
            if(x == 0)
            {
                x = 2;
            }
        }
        // si mov es mayor a 100 significa que nos quito una vida.
       if(mov >100)
       {
            // elimina al enemigo y regresa el nodo al origen.
            nodeR.detachAllChildren();
            nodeR.setLocalTranslation(0,0,0);
            mov= 0; // le asignamos 0
            vida--;//y decrementamos vida en una unidad.
            aparecerEnemigos(); // aparecemos mas enmigos.
        }
       // comprobamso si la vida es 0, si es asi mandamos la pantalla de perdiste
        if(vida == 0)
        {
            mov = -1;
            hudEstadoJuego();
        }
        // si contadorEnemigosMoridos es mayor o igual a 10 ganamos.
        if(contadorEnemigosMoridos >= 10)
        {
            mov= -1;
            hudEstadoJuego();
        }
       
    }// fin de metodo simpleUpdate 

    
    @Override
    public void simpleRender(RenderManager rm) 
    {
        // LLAMA A LOS PUNTOS. 
         hudPuntos(); 
    }

    
    private void aparecerEnemigos() 
    {
        int numeroRandom =(int)(Math.random()*5); // obtenemos un numero aleatorio  0 a 5. 
        nodeR.setLocalTranslation(0,0,0);// ponemos el nodo en el origen.
        mov= 0; 
        //depende de que numero aleatorio se creara un enemigo de determinado color y en ubicaciones diferentes de x.  
        switch (numeroRandom)
        {
            case 1:
            {
                nodeR.attachChild(this.enemigos("Blue_cube",new Vector3f(-10,2,0),ColorRGBA.Blue));
                rootNode.attachChild(nodeR);
                break;
            }

            case 2:
            {
                nodeR.attachChild(this.enemigos("Yellow_cube",new Vector3f(5,2,0) , ColorRGBA.Yellow));
                rootNode.attachChild(nodeR);
                break;
            }

            case 3:{
                nodeR.attachChild(this.enemigos("Red_cube",new Vector3f(0,2,0) , ColorRGBA.Red));
                rootNode.attachChild(nodeR);
                break;
            }
            default:{
                nodeR.attachChild(this.enemigos("Green_cube",new Vector3f(2,2,0),ColorRGBA.Green));
                rootNode.attachChild(nodeR);
                break;
            }

      }//cierra SWITCH
        
    }

   private void hudPuntos() 
    {
        hudText.setSize(guiFont.getCharSet().getRenderedSize()); // font size
        hudText.setColor(ColorRGBA.White);  // font color
        setDisplayStatView(false); // quitamos los datos inesesarios de la pantalla
        setDisplayFps(false); // quitamos el contador de fps
        // muestra el contador contadorEnemigosMoridos
        hudText.setText("ENEMIGOS MORIDOS: "+contadorEnemigosMoridos+"\n"+"VIDA ENEMIGOS: "+vida+"\n"+"PRECIONA 'R' PARA REINICIAR");             // the text
        hudText.setLocalTranslation(0, 700, 0); // position
        guiNode.attachChild(hudText);
    }// fin del HUD.
   
   private void hudEstadoJuego()
   {
       // si la vida es mayor a 0 imagen de ganamos de lo contrario imagen de perdemos
       if(vida >0)
       {
           pic.setImage(assetManager, "Textures/g.jpg", true);
       }else{
           pic.setImage(assetManager, "Textures/p.jpg", true);
       }
       pic.setWidth(settings.getWidth()/2);
       pic.setHeight(settings.getHeight()/2);
       pic.setPosition(settings.getWidth()/4, settings.getHeight()/4);
       guiNode.attachChild(pic);
   }
   public void reiniciar ()
   {
       //reinicia los contadores y elmimina la pantalla de ganar o perder
       contadorEnemigosMoridos = 0;
       vida = 3;
       mov = 0;
       guiNode.detachChild(pic);
   }
}