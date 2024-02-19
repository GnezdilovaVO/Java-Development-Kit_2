package server.server;

import server.client.ClientGUI;

import java.io.FileReader;
import java.io.FileWriter;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private boolean work;
    private List<ClientGUI> clientGUIList;
    private ServerView serverView;
    private static final String LOG_PATH = "src/server/server/log.txt";

    public Server(ServerView serverView) {
        this.serverView = serverView;
        clientGUIList = new ArrayList<>();
    }
    public void startServer(){
        if (work){
            serverView.appendLog("Сервер уже был запущен");
        } else {
            work = true;
            serverView.appendLog("Сервер запущен!");
        }
    }
    public void stopServer(){
        if (!work){
            serverView.appendLog("Сервер уже был остановлен");
        } else {
            work = false;
            while (!clientGUIList.isEmpty()){
                disconnectUser(clientGUIList.get(clientGUIList.size()-1));
            }
            serverView.appendLog("Сервер остановлен!");
        }
    }
    public boolean connectUser(ClientGUI clientGUI){
        if (!work){
            return false;
        }
        clientGUIList.add(clientGUI);
        return true;
    }
    public String getLog() {
        return readLog();
    }
    public void disconnectUser(ClientGUI clientGUI){
        clientGUIList.remove(clientGUI);
        if (clientGUI != null){
            clientGUI.disconnectedFromServer();
        }
    }
    public void message(String text){
        if (!work){
            return;
        }
        serverView.appendLog(text);
        answerAll(text);
        saveInLog(text);
    }
    private void answerAll(String text){
        for (ClientGUI clientGUI: clientGUIList){
            clientGUI.answer(text);
        }
    }
    private void saveInLog(String text){
        try (FileWriter writer = new FileWriter(LOG_PATH, true)){
            writer.write(text);
            writer.write("\n");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    private String readLog(){
        StringBuilder stringBuilder = new StringBuilder();
        try (FileReader reader = new FileReader(LOG_PATH);){
            int c;
            while ((c = reader.read()) != -1){
                stringBuilder.append((char) c);
            }
            stringBuilder.delete(stringBuilder.length()-1, stringBuilder.length());
            return stringBuilder.toString();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
