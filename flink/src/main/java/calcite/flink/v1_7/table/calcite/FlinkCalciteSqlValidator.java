package calcite.flink.v1_7.table.calcite;

import org.apache.calcite.adapter.java.JavaTypeFactory;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.sql.JoinType;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlInsert;
import org.apache.calcite.sql.SqlJoin;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperatorTable;
import org.apache.calcite.sql.validate.SqlConformanceEnum;
import org.apache.calcite.sql.validate.SqlValidatorCatalogReader;
import org.apache.calcite.sql.validate.SqlValidatorImpl;
import org.apache.calcite.sql.validate.SqlValidatorScope;
import org.apache.calcite.tools.ValidationException;

/**
 * full
 * @author quxiucheng
 * @date 2019-01-22 10:07:00
 */
public class FlinkCalciteSqlValidator extends SqlValidatorImpl {
    /**
     * Creates a validator.
     *
     * @param opTab Operator table
     * @param catalogReader Catalog reader
     * @param typeFactory Type factory
     */
    protected FlinkCalciteSqlValidator(SqlOperatorTable opTab, SqlValidatorCatalogReader catalogReader, JavaTypeFactory typeFactory) {
        super(opTab, catalogReader, typeFactory, SqlConformanceEnum.DEFAULT);
    }

    @Override
    protected RelDataType getLogicalTargetRowType(RelDataType targetRowType, SqlInsert insert) {
        return ((JavaTypeFactory) typeFactory).toSql(targetRowType);
    }

    @Override
    protected RelDataType getLogicalSourceRowType(RelDataType sourceRowType, SqlInsert insert) {
        return ((JavaTypeFactory) typeFactory).toSql(sourceRowType);
    }

    @Override
    protected void validateJoin(SqlJoin join, SqlValidatorScope scope) {
        // if (join.getJoinType == JoinType.LEFT &&
        //         isCollectionTable(join.getRight)) {
        //     join.getCondition match {
        //         case c:
        //             SqlLiteral if c.booleanValue() && c.getValue.asInstanceOf[Boolean] =>
        //             // We accept only literal true
        //         case c if null != c =>
        //             throw new ValidationException(
        //                     s"Left outer joins with a table function do not accept a predicate such as $c. " +
        //                     s"Only literal TRUE is accepted.")
        //     }
        // }
        if (join.getJoinType() == JoinType.LEFT
                && isCollectionTable(join.getRight())) {
            SqlNode condition = join.getCondition();
            if (null == condition) {
                throw new RuntimeException("Left outer joins with a table function do not accept a predicate such as Only literal TRUE is accepted.");
            }
            SqlLiteral sqlLiteral = (SqlLiteral) condition;
            if (sqlLiteral.booleanValue() && (Boolean) sqlLiteral.getValue()) {
                return;
            }
        }
        super.validateJoin(join, scope);
    }

    private boolean isCollectionTable(SqlNode node) {
        if (node instanceof SqlCall) {
            SqlKind kind = node.getKind();
            if (kind == SqlKind.AS) {
                return ((SqlCall) node).getOperandList().get(0).getKind() == SqlKind.COLLECTION_TABLE;
            }
        }
        return false;
    }

    // private def isCollectionTable(node:SqlNode): Boolean = {
    //     // TABLE (`func`(`foo`)) AS bar
    //     node match {
    //         case n:
    //             SqlCall if n.getKind == SqlKind.AS =>
    //             n.getOperandList.get(0).getKind == SqlKind.COLLECTION_TABLE
    //         case _ => false
    //     }
    // }
}
