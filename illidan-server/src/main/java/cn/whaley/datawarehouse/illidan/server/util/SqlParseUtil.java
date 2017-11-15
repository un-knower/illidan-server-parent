package cn.whaley.datawarehouse.illidan.server.util;

import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.ParseDriver;
import org.apache.hadoop.hive.ql.parse.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lituo on 2017/11/13.
 */
public class SqlParseUtil {

    private static Logger logger = LoggerFactory.getLogger(SqlParseUtil.class);

    public static Table parseCreateSql(String sql, String dbName) {

        if(sql == null || sql.isEmpty()) {
            throw new RuntimeException("sql语句为空");
        }

        if(sql.contains(";")) {
            throw new RuntimeException("只能解析单条语句，不能包含分号");
        }

        sql = sql.replace("`","");

        try {
            ParseDriver pd = new ParseDriver();
            ASTNode tree = pd.parse(sql);
            while ((tree.getToken() == null) && (tree.getChildCount() > 0)) {
                tree = (ASTNode) tree.getChild(0);
            }
//            System.out.print(tree.dump());
            if (!"TOK_CREATETABLE".equals(tree.getText())) {
                throw new RuntimeException("sql不是create语句");
            }

            Table table = new Table();
            for (Node node : tree.getChildren()) {

                ASTNode treeNode = (ASTNode) node;

                if (treeNode.getText().equals("TOK_TABNAME")) {
                    if (treeNode.getChildCount() == 1) {
                        if (dbName == null || dbName.isEmpty()) {
                            throw new RuntimeException("请提供数据库名");
                        }
                        table.tableCode = treeNode.getChild(0).getText();
                        table.dbCode = dbName;
                    } else if (treeNode.getChildCount() == 2) {
                        table.tableCode = treeNode.getChild(1).getText();
                        table.dbCode = treeNode.getChild(0).getText();
                    } else {
                        throw new RuntimeException("语法错误，库表名不合法");
                    }

                }

                if (treeNode.getText().equals("TOK_LIKETABLE")) {
                    if (treeNode.getChildCount() > 0) {
                        throw new RuntimeException("不支持create table like");
                    }
                }

                if (treeNode.getText().equals("TOK_TABCOLLIST")) {
                    parseCols(treeNode, table, false);
                }

                if (treeNode.getText().equals("TOK_TABLECOMMENT")) {
                    table.comment = treeNode.getChild(0).getText();
                }

                if (treeNode.getText().equals("TOK_TABLEPARTCOLS")) {
                    if (treeNode.getChildCount() > 0) {
                        parseCols((ASTNode) treeNode.getChild(0), table, true);
                    }
                }

            }

            return table;

        } catch (ParseException e) {
            throw new RuntimeException("sql解析失败：" + e.getMessage());
        }
    }

    private static void parseCols(ASTNode treeNode, Table table, boolean partitionColumn) {
        if (treeNode.getText().equals("TOK_TABCOLLIST")) {
            if (treeNode.getChildCount() == 0) {
                throw new RuntimeException("未解析到字段");
            }
            for (Node colNode : treeNode.getChildren()) {
                ASTNode tableColNode = (ASTNode) colNode;
                if (!tableColNode.getText().equals("TOK_TABCOL")) {
                    continue;
                }
                int childCount = tableColNode.getChildCount();
                if (childCount < 2) {
                    throw new RuntimeException("列解析异常");
                }
                Field field = new Field();
                field.fieldCode = tableColNode.getChild(0).getText();
                field.fieldType = tableColNode.getChild(1).getText().substring(4).toLowerCase();
                if (childCount == 2) {
                    field.comment = "";
                } else {
                    field.comment = tableColNode.getChild(2).getText();
                }
                field.partitionColumn = partitionColumn;
                table.fields.add(field);
            }
        }
    }

    public static class Table {
        public String tableCode;
        public String dbCode;
        public String comment;
        public List<Field> fields = new ArrayList<>();
    }

    public static class Field {
        public String fieldCode;
        public String fieldType;
        public Boolean partitionColumn;
        public String comment;
    }

}
