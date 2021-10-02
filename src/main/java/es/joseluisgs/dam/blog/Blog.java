package es.joseluisgs.dam.blog;

import es.joseluisgs.dam.blog.controller.CategoryController;
import es.joseluisgs.dam.blog.controller.CommentController;
import es.joseluisgs.dam.blog.controller.PostController;
import es.joseluisgs.dam.blog.controller.UserController;
import es.joseluisgs.dam.blog.database.DataBaseController;
import es.joseluisgs.dam.blog.dto.CategoryDTO;
import es.joseluisgs.dam.blog.dto.CommentDTO;
import es.joseluisgs.dam.blog.dto.PostDTO;
import es.joseluisgs.dam.blog.dto.UserDTO;
import es.joseluisgs.dam.blog.model.Category;
import es.joseluisgs.dam.blog.model.Post;
import es.joseluisgs.dam.blog.model.User;
import es.joseluisgs.dam.blog.utils.ApplicationProperties;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Blog {
    private static Blog instance;

    private Blog() {
    }

    public static Blog getInstance() {
        if (instance == null) {
            instance = new Blog();
        }
        return instance;
    }

    public void checkService() {
        DataBaseController controller = DataBaseController.getInstance();
        controller.open();
        ResultSet rs = controller.query("SELECT * from test");
        try {
            rs.first();
            controller.close();
        } catch (SQLException e) {
            System.err.println("Error al arrancar Base de Datos: " + e.getMessage());
            System.exit(1);
        }
    }

    public void initDataBase() {
        String sqlFile =  System.getProperty("user.dir")+ File.separator+"sql"+File.separator+"blog.sql";
        System.out.println(sqlFile);
        DataBaseController controller = DataBaseController.getInstance();
        controller.open();
        try {
            controller.initData(sqlFile);
            controller.close();
        } catch (FileNotFoundException e) {
            System.err.println("Error al arrancar Base de Datos: " + e.getMessage());
            System.exit(1);
        }
    }

    public void Categories() {
        CategoryController categoryController = CategoryController.getInstance();
        // Obtenemos todas las categorías
        // List<CategoryDTO> categories = categoryController.getAllCategories();
        // categories.forEach(c-> System.out.println(c.toJSON()));

        System.out.println("GET Todas las categorías");
        System.out.println(categoryController.getAllCategoriesJSON());

        System.out.println("GET Categoría con ID = 2");
        System.out.println(categoryController.getCategoryByIdJSON(2L));

        System.out.println("POST Insertando Categoría");
        CategoryDTO categoryDTO = CategoryDTO.builder()
                .texto("Prueba " + Instant.now().toString())
                .build();
        System.out.println(categoryController.postCategoryJSON(categoryDTO));

        System.out.println("UPDATE Categoría con ID = 4");
        categoryDTO = CategoryDTO.builder()
                .id(4L)
                .texto("Prueba Update")
                .build();
        System.out.println(categoryController.updateCategoryJSON(categoryDTO));

        System.out.println("DELETE Categoría con ID = 4");
        categoryDTO = CategoryDTO.builder()
                .id(4L)
                .build();
        System.out.println(categoryController.deleteCategoryJSON(categoryDTO));
    }

    public void Users() {
        UserController userController = UserController.getInstance();

        System.out.println("GET Todos los usuarios");
        System.out.println(userController.getAllUsersJSON());

        System.out.println("GET Usuario con ID = 2");
        System.out.println(userController.getUserByIdJSON(2L));

        System.out.println("POST Insertando Usuario");
        UserDTO userDTO = UserDTO.builder()
                .nombre("Nombre " + Instant.now().toString())
                .email("user" + Math.random() + "@mail.com")
                .password("1234")
                .fechaRegistro(LocalDate.now())
                .build();
        System.out.println(userController.postUserJSON(userDTO));

        System.out.println("UPDATE Usuario con ID = 5");
        userDTO = UserDTO.builder()
                .id(5L)
                .nombre("Prueba Update")
                .email("prueba@update.com")
                .build();
        System.out.println(userController.updateUserJSON(userDTO));

        System.out.println("DELETE User con ID = 5");
        userDTO = UserDTO.builder()
                .id(5L)
                .build();
        System.out.println(userController.deleteUserJSON(userDTO));
    }

    public void Posts() {
        PostController postController = PostController.getInstance();

        System.out.println("GET Todos los Post");
        System.out.println(postController.getAllPostJSON());

        System.out.println("GET Post con ID = 2");
        System.out.println(postController.getPostByIdJSON(2L));

        System.out.println("POST Insertando Post");
        PostDTO postDTO = PostDTO.builder()
                .titulo("Post " + Instant.now().toString())
                .url("http://" + Math.random() + ".dominio.com")
                .contenido(Instant.now().toString())
                .fechaPublicacion(LocalDateTime.now())
                .build();
        // Asignamos el usuario que exista, solo necesitamos su ID
        User postUser = User.builder()
                .id(1L)
                .nombre("Pepe Perez")
                .email("pepe@pepe.com")
                .build();
        postDTO.setUser(postUser);
        // Asignamos una categoria que exita
        Category categoryPost = Category.builder()
                .id(1L)
                .texto("General")
                .build();
        postDTO.setCategory(categoryPost);

        System.out.println(postController.postPostJSON(postDTO));

        System.out.println("UPDATE Post con ID = 4");
        // Solo dejamos cambiar el tútulo o el contenido
        postDTO = PostDTO.builder()
                .id(4L)
                .titulo("Update " + Instant.now().toString())
                .contenido("Update " + Instant.now().toString())
                .url("http://" + Math.random() + ".dominio.com")
                .fechaPublicacion(LocalDateTime.now())
                .build();
        postDTO.setUser(postUser);
        postDTO.setCategory(categoryPost);
        System.out.println(postController.updatePostJSON(postDTO));

        System.out.println("DELETE Post con ID = 4");
        postDTO = PostDTO.builder()
                .id(5L)
                .build();
        postDTO.setUser(postUser);
        postDTO.setCategory(categoryPost);
        System.out.println(postController.deletePostJSON(postDTO));

    }


    public void Comments() {
        CommentController commentController = CommentController.getInstance();

        System.out.println("GET Todos los Comentarios");
        System.out.println(commentController.getAllCommentsJSON());

        System.out.println("GET Comentario con ID = 2");
        System.out.println(commentController.getCommentByIdJSON(2L));

        System.out.println("POST Insertando Comentario");
        CommentDTO commentDTO = CommentDTO.builder()
                .texto("Comentario " + Instant.now().toString())
                .fechaPublicacion(LocalDateTime.now())
                .build();
        // Asignamos el usuario que exista, solo necesitamos su ID
        User commentUser = User.builder()
                .id(1L)
                .nombre("Pepe Perez")
                .email("pepe@pepe.com")
                .build();
        commentDTO.setUser(commentUser);
        // Asignamos una post que exita
        Post commentPost = Post.builder()
                .id(3L)
                .titulo("Post num 3")
                .url("http://post3.com")
                .contenido("Este es el post num 3")
                .fechaPublicacion(LocalDateTime.now())
                .user_id(3L)
                .category_id(3L)
                .build();
        commentDTO.setPost(commentPost);

        System.out.println(commentController.postCommentJSON(commentDTO));

        System.out.println("UPDATE Comentario con ID = 7");
        // Solo dejamos cambiar el tútulo o el contenido
        commentDTO = CommentDTO.builder()
                .id(4L)
                .texto("Update " + Instant.now().toString())
                .fechaPublicacion(LocalDateTime.now())
                .build();
        commentDTO.setUser(commentUser);
        commentDTO.setPost(commentPost);
        System.out.println(commentController.updateCommentJSON(commentDTO));

        System.out.println("DELETE Comentario con ID = 7");
        commentDTO = CommentDTO.builder()
                .id(7L)
                .build();
        commentDTO.setUser(commentUser);
        commentDTO.setPost(commentPost);
        System.out.println(commentController.deleteCommentJSON(commentDTO));
    }

}
