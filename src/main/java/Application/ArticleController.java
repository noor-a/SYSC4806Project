package Application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ArticleController {
    public static String UPLOAD_DIR = System.getProperty("user.dir")+"/uploads";

    @Autowired
    ArticleRepository articleRepository;
    @RequestMapping(value = "/viewUploads/{title}", method = RequestMethod.GET)
    @ResponseBody
    public FileSystemResource getFile(@PathVariable("title") String title) {
        return new FileSystemResource(articleRepository.findByTitle(title).get(0).getFile());
    }
    @GetMapping("/viewUploads")
    public String viewUploads(Model model){
        model.addAttribute("articles", articleRepository.findAll());
        return "viewUploads";
    }

    @RequestMapping(value="/upload", method = RequestMethod.GET)
    public String uploadHandler(Model model){

        return "uploadForm";
    }

    @RequestMapping(value="/upload", method = RequestMethod.POST)
    public String submit(@RequestParam("file") MultipartFile file, Model model){
        model.addAttribute("file", file);

        //Creates a new article with the file and saves it to the repository
        File convFile = new File(file.getOriginalFilename());
        try {
            convFile.createNewFile();
            FileOutputStream fos;

            fos = new FileOutputStream(convFile);

            fos.write(file.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Article a = new Article();
        a.setTitle(file.getOriginalFilename());
        a.setFile(convFile);
//        a.setStatus(Status.SUMBITRED);
        articleRepository.save(a);
//        return "uploadView";
        return "uploadView";
    }

    @GetMapping("/uploadView")
    public String getUploadedFiles(Model model){
        return "uploadView";
    }

    //Gets Returns all articles that have been uploaaded
    public List<Article> getAllArticles(){
        return articleRepository.findAll();
    }
}