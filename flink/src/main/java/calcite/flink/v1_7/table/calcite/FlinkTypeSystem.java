package calcite.flink.v1_7.table.calcite;

import org.apache.calcite.rel.type.RelDataTypeSystemImpl;
import org.apache.calcite.sql.type.SqlTypeName;

/**
 * @author quxiucheng
 * @date 2019-01-22 15:42:00
 */
public class FlinkTypeSystem extends RelDataTypeSystemImpl{

    @Override
    public int getMaxNumericScale() {
        return Integer.MAX_VALUE / 2;
    }

    @Override
    public int getMaxNumericPrecision() {
        return Integer.MAX_VALUE / 2;
    }

    @Override
    public int getDefaultPrecision(SqlTypeName typeName) {
        if (typeName == SqlTypeName.VARCHAR) {
            return Integer.MAX_VALUE;
        }
        if (typeName == SqlTypeName.TIMESTAMP) {
            return 3;
        }
        return super.getDefaultPrecision(typeName);
    }
}
