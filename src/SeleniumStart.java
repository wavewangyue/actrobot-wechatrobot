
/**
 * Created by wangyue on 2016/12/14.
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class SeleniumStart {

        public static void main(String[] args) throws InterruptedException,IOException {

            String myWeChatName = "小航";
            String filePath= "C:\\Users\\wangyue\\Desktop\\weChatRobot\\history.txt";
            String chromedriverPath = System.getProperty("user.dir") + "\\lib\\chromedriver.exe";

            System.setProperty("webdriver.chrome.driver",chromedriverPath);
            WebDriver driver = new ChromeDriver();
            driver.get("https://wx.qq.com/");
            System.out.println("Info:已打开网页版微信，等待扫码登陆");
            driver.getTitle();
            System.out.println("Info:已登陆网页版微信，等待锁定监控对象");
            WebElement groupname;
            while (true){
                groupname = driver.findElement(By.xpath("/html/body/div[@class='main']/div/div[2]/div/div[1]/div[2]/div/a"));
                if (!groupname.getText().equals("")) break;
                Thread.sleep(300);
            }
            System.out.println("Info:已锁定监控对象，对象名称为"+groupname.getText());
            WebElement bubblearea = driver.findElement(By.xpath("/html/body/div[@class='main']/div/div[2]/div/div[2]/div[1]/div[1]"));
            WebElement textarea = driver.findElement(By.xpath("/html/body/div[@class='main']/div/div[2]/div/div[3]/div[2]/pre"));
            WebElement sendbutton = driver.findElement(By.xpath("/html/body/div[@class='main']/div/div[2]/div/div[3]/div[3]/a"));

            //循环监控开始
            //用来记录最新一条信息，用来判断是否有信息更新
            String lastactor = "";
            String lastcontent = "";
            while (true){
                List<WebElement> bubbles = bubblearea.findElements(By.xpath("./*"));
                if (bubbles.size() <= 2){
                    Thread.sleep(300);
                    continue;
                }
                //如果消息是图片或者语音
                String content = "";
                String actor = "";
                try{
                    content = driver.findElement(By.xpath("/html/body/div[@class='main']/div/div[2]/div/div[2]/div[1]/div[1]/div["+(bubbles.size()-1)+"]/div/div/div/div/div/div/div/pre")).getText();
                    actor = driver.findElement(By.xpath("/html/body/div[@class='main']/div/div[2]/div/div[2]/div[1]/div[1]/div["+(bubbles.size()-1)+"]/div/div/div/img")).getAttribute("title");
                }catch (Exception e){
                    Thread.sleep(300);
                    continue;
                }
                Date datenow = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                String time = dateFormat.format(datenow);
                //检查消息是否有更新
                if (!actor.equals(lastactor)||(!content.equals(lastcontent))){
                    lastactor = actor;
                    lastcontent = content;
                    if (content.indexOf("@"+myWeChatName)!=-1){
                        //被@到，读数据
                        BufferedReader fin = new BufferedReader (new FileReader(filePath));
                        String line = null;
                        String result = null;
                        while ((line = fin.readLine()) != null){
                            String[] hiscontent = line.split("\t");
                            if (hiscontent[1].equals(actor)){
                                result = hiscontent[2];
                            }
                        }
                        if (result != null){
                            textarea.sendKeys(result);
                            sendbutton.click();
                        }
                        fin.close();
                    }else{
                        //没有@，存数据
                        if (!content.equals("")){
                            BufferedWriter fout = new BufferedWriter(new FileWriter(filePath,true));
                            fout.write(time+"\t"+actor+"\t"+content+"\n");
                            fout.close();
                        }
                    }
                }
                Thread.sleep(300);
            }
        }

    }
