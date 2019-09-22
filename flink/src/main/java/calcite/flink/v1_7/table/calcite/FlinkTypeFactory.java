package calcite.flink.v1_7.table.calcite;

import org.apache.calcite.jdbc.JavaTypeFactoryImpl;
import org.apache.calcite.rel.type.RelDataTypeSystem;

/**
 * @author quxiucheng
 * @date 2019-01-22 10:06:00
 */
public class FlinkTypeFactory extends JavaTypeFactoryImpl {
    public FlinkTypeFactory(RelDataTypeSystem typeSystem) {
        super(typeSystem);
    }


}
