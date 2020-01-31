package com.philpot.nowplayinghistory.db

/**
 * Created by colse on 10/29/2017.
 */
object SQLiteTableBuilder {

    fun builder(): SQLiteCreateTable {
        return SQLiteCreateTable()
    }

    class SQLiteCreateTable {

        private var tableIdBuilder: TableIdBuilder? = null

        fun tableName(tableName: String): TableIdBuilder {
            tableIdBuilder = TableIdBuilder(tableName)
            return tableIdBuilder as TableIdBuilder
        }
    }

    class TableIdBuilder (private val tableName: String) {

        fun integerPrimaryKey(idCol: String): TableCompleteBuilder {
            return TableCompleteBuilder(tableName, idCol, "INTEGER", false)
        }

        fun integerAutoIncrement(idCol: String): TableCompleteBuilder {
            return TableCompleteBuilder(tableName, idCol, "INTEGER", true)
        }

        fun textPrimaryKey(idCol: String): TableCompleteBuilder {
            return TableCompleteBuilder(tableName, idCol, "TEXT", false)
        }

        fun multiPrimaryKey(vararg colNames: String): TableCompleteBuilder {
            return TableCompleteBuilder(tableName, *colNames)
        }
    }

    class TableCompleteBuilder {

        private val stringBuilder = StringBuilder(110)
        private val multiColumnColNames: MutableList<String> = arrayListOf()

        constructor(tableName: String, vararg multiKeyColNames: String) {
            stringBuilder.append("CREATE TABLE ").append(tableName).append(" (")
            multiColumnColNames += multiKeyColNames
        }

        constructor(tableName: String, idCol: String, idType: String, autoIncrement: Boolean) {
            stringBuilder.append("CREATE TABLE ").append(tableName).append(" (")
            stringBuilder.append(idCol).append(" ").append(idType).append(" PRIMARY KEY ")
            if (autoIncrement) {
                stringBuilder.append("AUTOINCREMENT ")
            } else {
                stringBuilder.append("NOT NULL")
            }
            stringBuilder.append(", ")
        }

        @JvmOverloads fun textColumn(colName: String, nullable: Boolean = true): TableCompleteBuilder {
            stringBuilder.append(colName).append(" TEXT")
            appendNotNullable(nullable)
            stringBuilder.append(", ")
            return this
        }

        @JvmOverloads fun intColumn(colName: String, nullable: Boolean = true): TableCompleteBuilder {
            stringBuilder.append(colName).append(" INTEGER")
            appendNotNullable(nullable)
            stringBuilder.append(", ")
            return this
        }

        private fun appendNotNullable(nullable: Boolean) {
            if (!nullable) {
                stringBuilder.append(" NOT NULL")
            }
        }

        @JvmOverloads fun booleanColumn(colName: String, nullable: Boolean = false): TableCompleteBuilder {
            stringBuilder.append(colName).append(" INTEGER")
            appendNotNullable(nullable)
            stringBuilder.append(", ")
            return this
        }

        @JvmOverloads fun realColumn(colName: String, nullable: Boolean = true): TableCompleteBuilder {
            stringBuilder.append(colName).append(" REAL")
            appendNotNullable(nullable)
            stringBuilder.append(", ")
            return this
        }

        fun textVarcharColumn(colName: String, length: Int): TableCompleteBuilder {
            stringBuilder.append(colName).append(" VARCHAR(").append(length).append(")")
            appendNotNullable(true)
            stringBuilder.append(", ")
            return this
        }

        fun build(): String {
            stringBuilder.replace(stringBuilder.length - 2, stringBuilder.length, "") //To remove the last comma
            if (multiColumnColNames.isNotEmpty()) {
                stringBuilder.append(", PRIMARY KEY (")
                var separator = ""
                for (colname in multiColumnColNames) {
                    stringBuilder.append(separator).append(colname)
                    separator = ", "
                }
                stringBuilder.append(")")
            }
            return stringBuilder.append(")").toString()
        }

    }
}