package com.devXmachina.dxmgame.gamelogic;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.devXmachina.dxmgame.GameLoader;
//import org.json.*;

//La classe principal, aquesta guarda tota la informació i funcions relacionades amb un arbre, cada arbre són les prediccions
//de la IA respecte a una resposta, pel que hi hauran tants arbres com respostes s'intentin predir
public class TreeScript
{
    int treeSize = 10; //Es el màxim de capes de profunditat de l'arbre
    Branch mainBranch = new Branch();
    String finalResult;

    //------------------- Trobar el que ha predit Weka a partir de les decision escollides--------------------
    ResultStruct findResult(String[] id, String[] path)
    {
        Branch currentBranch = mainBranch;
        //Reitera el mateix procés de, mirar la branch a la que està, mirar a quina pasar i avançar fins trobar la final
        for (int i = 0; i <= treeSize; i++)
        {
            //Fa unes repeticions per trobar quin es el camí correcte
            for (int x = id.length - 1; x >= 0; x--)
            {
                if (id[x].equals(currentBranch.id))
                {
                    currentBranch = currentBranch.getBranch(path[x]);
                    if (currentBranch == null)  return new ResultStruct();

                    //Si ha arribat al final de la branca retorna els resultats
                    if (currentBranch.endOfTree)  return currentBranch.getResult();
                }
            }
        }

        return new ResultStruct();
    }

    //------------------------Carregar un Json a un arbre----------------------------
    void loadJson(String jsonString)
    {
        JsonReader json = new JsonReader();
        JsonValue obj = json.parse(jsonString);
        JsonValue currentObj = obj;

        //Lo profund que es el currentBranch a l'arbre
        int deepness;

        //Prepara el mainBranch i una mica del primer subbranch
        mainBranch.id = currentObj.getString("id");
        mainBranch.amountOfSub = currentObj.get("subBranches").size;
        mainBranch.initializeBranch(currentObj.get("subBranches").size);
        mainBranch.amountOfSub = currentObj.get("subBranches").size;
        Branch currentBranch = mainBranch.subBranches[0];
        currentBranch.parentBranch = mainBranch;
        int[] pathRecord = new int[10];
        deepness = 1;
        int[] selectedBranch = new int[10];
        currentObj = currentObj.get("subBranches").get(0);
        Branch prevBranch = mainBranch;
        //Repeteix aquests passos fins a construir l'arbre sencer
        while (true) {
            //Veure si es un final sense sortida
            currentBranch.parentBranch = prevBranch;
            setBranchData(currentBranch,currentObj);

            if (currentObj.getString("EndofTree").equals("true")) {

                //Anar-se cap enrere si hem arribat a un final de l'arbre
                while (true) {

                    //Anar-se enrere una vegada
                    deepness--;
                    /*Si ja ha configurat tots els branches, acaba l'operació (això es detecta ja que quan es va enrere, com
                    que totes les branques estàn ocupades al 100%, es va enrere una vegada més a una profunditat negativa*/
                    if (deepness == -1) break;
                    currentBranch = currentBranch.parentBranch;
                    currentObj = setbackCurrentObj(currentObj, obj, pathRecord, deepness);
                    selectedBranch[deepness]++;
                    /*Si tots els branches d'aquesta linea estan configurats, reseteja el branch que s'escollirà a aquesta
                    profunditat i espera al següent cicle del while per tornar enrere
                     */
                    if (selectedBranch[deepness] >= currentObj.get("subBranches").size) {
                        selectedBranch[deepness] = 0;
                    } else {
                        //Si ha arribat a un branch que ell encara no s'ha configurat, entra a aquest i es prepara per a la següent ronda
                        pathRecord[deepness] = selectedBranch[deepness];
                        deepness++;
                        prevBranch = currentBranch;
                        currentBranch = currentBranch.subBranches[selectedBranch[deepness -1]];
                        currentObj = currentObj.get("subBranches").get(selectedBranch[deepness - 1]);


                        break;
                    }
                }
            } else {
                //Avançar a la següent branca
                deepness++;
                prevBranch = currentBranch;
                currentBranch = currentBranch.subBranches[0];
                currentObj = currentObj.get("subBranches").get(0);
            }
            /*Torna a chequejar si la profunditat es negativa per sortir d'aquest bucle també*/
            if (deepness == -1) break;
        }
    }

    //Es un algoritme utilitzat al loadJson per anar enrere un objecte a l'arbre de Json, com que un objecte Json no pot
    //referenciar el seu "pare", he fet una funció a part
    JsonValue setbackCurrentObj(JsonValue obj,JsonValue fullJson, int[] path, int deepness) {
        obj = fullJson;
        for (int i = 0; i < deepness ; i++) {
            obj = obj.get("subBranches").get(path[i]);
        }

        return obj;
    }
    //Configura tota la data d'un branch (excepte el parent, que l'ha de configurar abans)
    void setBranchData(Branch currentBranch, JsonValue currentObj) {
        currentBranch.endOfTree = (currentObj.getString("EndofTree").equals("true"));
        //Les dades son diferents si es tracta del final de l'arbre o no
        if (currentBranch.endOfTree) {
            currentBranch.branchEnd =  currentObj.getString("branchEnd");
            currentBranch.ratio = getRatioFloat(currentObj.getString("ratio")) ;

        } else {
            currentBranch.id = currentObj.getString("id");
            currentBranch.initializeBranch(currentObj.get("subBranches").size);
            currentBranch.amountOfSub = currentObj.get("subBranches").size;
        }
        currentBranch.pathId = currentObj.getString("pathId");
    }

    float[] getRatioFloat(String str) {
        float[] ratio = new float[2];
        ratio [0] = Float.parseFloat(str.split("/")[0]);
        ratio [1] = Float.parseFloat(str.split("/")[1]);
        return  ratio;
    }
    //-----------------------------main static function para provar el sistema------------------------
    public ResultStruct get_mlAnswer(GameLoader gameLoader, GameEvent gameEvent) {
        int mlId= gameEvent.ml;
        TreeScript tree = new TreeScript();
        //Aquests es un cas d'exemple per veure el funcionament
        //String exampleTree ="{\"id\":\"decision1\",\"EndofTree\":\"false\",\"pathId\":\"\",\"subBranches\":[{\"id\":\"decision2\",\"EndofTree\":\"false\",\"pathId\":\"si\",\"subBranches\":[{\"EndofTree\":\"true\",\"pathId\":\"si\",\"branchEnd\":\"si\",\"ratio\":\"624.0/24.0\"},{\"EndofTree\":\"true\",\"pathId\":\"no\",\"branchEnd\":\"no\",\"ratio\":\"72.0/0.0\"}]},{\"EndofTree\":\"true\",\"pathId\":\"no\",\"branchEnd\":\"si\",\"ratio\":\"624.0/24.0\"}]}";
        String exampleTree= Gdx.files.internal("JSON/JSON"+mlId+".txt").readString();
        //String exampleTree= "{\"id\": \"decision5\",\"EndofTree\": \"false\",\"pathId\": \"\",\"subBranches\": [{\"id\" : \"decision2\",\"EndofTree\" : \"false\",\"pathId\" : \"a\",\"subBranches\" : [{\"id\" : \"decision3\",\"EndofTree\" : \"false\",\"pathId\" : \"a\",\"subBranches\" : [{\"id\" : \"decision4\",\"EndofTree\" : \"false\",\"pathId\" : \"a\",\"subBranches\" : [{\"EndofTree\": \"true\",\"pathId\": \"a\",\"branchEnd\": \"b\",\"ratio\": \"2.0/0.0\"},{\"EndofTree\": \"true\",\"pathId\": \"b\",\"branchEnd\": \"a\",\"ratio\": \"5.0/2.0\"}]},{\"EndofTree\": \"true\",\"pathId\": \"b\",\"branchEnd\": \"a\",\"ratio\": \"4.0/0.0\"}]},{\"EndofTree\": \"true\",\"pathId\": \"b\",\"branchEnd\": \"b\",\"ratio\": \"16.0/4.0\"}]},{\"EndofTree\": \"true\",\"pathId\": \"b\",\"branchEnd\": \"a\",\"ratio\": \"7.0/2.0\"}]}";
        tree.loadJson(exampleTree);
        String[] id = {"decision1", "decision2", "decision3","decision4","decision5"};
        String[] path =gameLoader.testAnswer;
        //System.out.println( tree.findResult(id, path).result + "x" + tree.mainBranch.subBranches[0].subBranches[0].branchEnd);
        ResultStruct resultInfo = tree.findResult(id, path);
        resultInfo.gameEvent_id=gameEvent.getId();
        System.out.println( "Ha trobat \"" + resultInfo.result + "\" amb una confiança de " + (float) Math.round((tree.findResult(id, path).ratio * 10000))/100 + "%");
        return resultInfo;
    }

    //-------------------------------------NOMÉS DE PROVA--------------------------------------------------------
    TreeScript[] createTreeArray(String[] jsonStringArray) {
        TreeScript[] auxTree = new TreeScript[jsonStringArray.length];
        for (int i = 0; i < jsonStringArray.length; i++) {
            auxTree[i].loadJson(jsonStringArray[i]);
        }
        return auxTree;
    }

}

//Class per cada branch
class Branch
{
    public String id = null;
    Branch[] subBranches = new Branch[10];
    Branch parentBranch;
    public int amountOfSub;
    String branchEnd;
    float[] ratio = new float[2];
    public boolean endOfTree;
    String pathId;

    //Funció per trobar una subbranch que tingui la mateixa pathId (per exemple "si", "no") que la donada
    public Branch getBranch (String path)
    {
        for (int i = 0; i <= 10; i++)
        {
            if (subBranches[i].pathId.equals(path))
            {
                return subBranches[i];
            }
        }
        return null;
    }

    //A causa de la natura d'utilitzar un array d'un objecte a dins de si mateix, aquest array s'ha d'inicialitzar
    //com una sèrie d'objectes a part abans de ser utilitzables
    public void initializeBranch(int length) {
        for (int i = 0; i < length; i++) {
            subBranches[i] = new Branch();
        }
    }


    //Retorna un ResultStruct a partir del final de la branca i el ratio d'aquesta
    public ResultStruct getResult()
    {
        ResultStruct result = new ResultStruct();
        result.result = branchEnd;
        result.ratio =  ((ratio[0] - ratio[1]) / ratio[0]);
        return result;
    }
}
