package calcite.flink.v1_1.table;

import org.apache.calcite.rel.type.RelDataTypeSystemImpl;

/**
 * @author quxiucheng
 * @date 2019-01-21 17:07:00
 */
public class FlinkTypeSystem extends RelDataTypeSystemImpl {

    @Override
    public int getMaxNumericScale() {
        return Integer.MAX_VALUE / 2;
    }

    @Override
    public int getMaxNumericPrecision() {
        return Integer.MAX_VALUE / 2;
    }
}
