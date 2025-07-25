// package com.acg.util;

// import org.jsoup.Jsoup;
// import org.jsoup.nodes.Document;
// import org.jsoup.nodes.Element;
// import org.jsoup.select.Elements;

// import java.io.File;
// import java.io.FileWriter;
// import java.io.IOException;
// import java.nio.charset.StandardCharsets;
// import java.util.concurrent.atomic.AtomicInteger;

// /**
//  * 浏览器收藏夹HTML文件导入工具
//  * <p>
//  * 使用方法:
//  * 1. 将 favorites_*.html 文件放到项目根目录
//  * 2. 修改下面的 FILE_PATH 为你的文件名
//  * 3. 运行 main 方法
//  * 4. 将控制台输出的 SQL 语句复制到数据库客户端执行
//  * </p>
//  */
// public class BookmarkImporter {

//     // ---! 请修改为你的收藏夹文件名 !---
//     private static final String FILE_PATH = "favorites_2025_7_21.html";
//     // ---! 请根据你的数据库情况修改 !---
//     private static final int CREATED_BY_USER_ID = 1; // 默认创建者为ID=1的用户
//     private static final int CATEGORY_START_ID = 100; // 分类ID起始值，避免与现有ID冲突
//     private static final int RESOURCE_START_ID = 100; // 资源ID起始值

//     private static final AtomicInteger categoryIdCounter = new AtomicInteger(CATEGORY_START_ID);
//     private static final AtomicInteger resourceIdCounter = new AtomicInteger(RESOURCE_START_ID);

//     private static final String OUTPUT_SQL_FILE = "import_bookmarks.sql";

//     public static void main(String[] args) throws IOException {
//         File input = new File(FILE_PATH);
//         if (!input.exists()) {
//             System.err.println("错误：找不到文件 " + FILE_PATH + "，请确保它在项目根目录下。");
//             return;
//         }

//         try (FileWriter writer = new FileWriter(OUTPUT_SQL_FILE, StandardCharsets.UTF_8)) {
//             writer.write("-- ----------------------------\n");
//             writer.write("-- -- ACG Resource Platform Bookmark Import SQL\n");
//             writer.write("-- -- Generated on " + java.time.LocalDateTime.now() + "\n");
//             writer.write("-- -- PLEASE BACKUP YOUR DATABASE BEFORE EXECUTION!\n");
//             writer.write("-- ----------------------------\n\n");

//             Document doc = Jsoup.parse(input, "UTF-8");
//             // 通常书签都在第一个 H1 标签后的 DL 列表里
//             Element rootDl = doc.selectFirst("h1 ~ dl");
//             if (rootDl != null) {
//                 parseDl(rootDl, 0, writer); // 0 表示顶级分类
//             } else {
//                 System.err.println("错误：无法在HTML文件中找到收藏夹根目录 (<DL>)。");
//             }
//             System.out.println("成功！SQL 已生成到文件: " + OUTPUT_SQL_FILE);
//         } catch (IOException e) {
//             System.err.println("写入 SQL 文件时出错: " + e.getMessage());
//         }
//     }

//     private static void parseDl(Element dl, int parentCategoryId, FileWriter writer) throws IOException {
//         // 直接子元素是 DT
//         Elements dts = dl.children();
//         for (Element dt : dts) {
//             if (!dt.tagName().equalsIgnoreCase("dt")) continue;

//             Element h3 = dt.selectFirst("h3");
//             Element a = dt.selectFirst("a");
//             Element nestedDl = dt.selectFirst("dl");

//             if (h3 != null) {
//                 // 这是一个文件夹 -> resource_category
//                 int currentCategoryId = categoryIdCounter.getAndIncrement();
//                 String categoryName = escapeSql(h3.text());
//                 String categoryCode = categoryName.toLowerCase().replaceAll("\\s+", "-");

//                 String sql = String.format(
//                         "INSERT INTO `resource_category` (id, name, code, description, icon, sort_order, parent_id, status, created_time, updated_time, deleted) VALUES (%d, '%s', '%s', '%s', 'fas fa-folder', 0, %d, 1, NOW(), NOW(), 0);",
//                         currentCategoryId, categoryName, categoryCode, categoryName, parentCategoryId
//                 );
//                 writer.write(sql + "\n");

//                 if (nestedDl != null) {
//                     parseDl(nestedDl, currentCategoryId, writer);
//                 }
//             } else if (a != null) {
//                 // 这是一个链接 -> resource
//                 int resourceId = resourceIdCounter.getAndIncrement();
//                 String resourceName = escapeSql(a.text());
//                 String resourceUrl = escapeSql(a.attr("href"));
//                 String description = resourceName; // 默认描述和名称一样

//                 String sql = String.format(
//                         "INSERT INTO `resource` (id, name, description, url, category_id, status, sort_order, created_by, created_time, updated_time, deleted, view_count, like_count) VALUES (%d, '%s', '%s', '%s', %d, 1, 0, %d, NOW(), NOW(), 0, 0, 0);",
//                         resourceId, resourceName, description, resourceUrl, parentCategoryId, CREATED_BY_USER_ID
//                 );
//                 writer.write(sql + "\n");
//             }
//         }
//     }

//     private static String escapeSql(String s) {
//         if (s == null) {
//             return "";
//         }
//         return s.replace("'", "''");
//     }
// } 