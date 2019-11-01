package calcite.flink.v1_7.table.plan;

import calcite.flink.v1_7.table.plan.nodes.datastream.DataStreamRel;
import calcite.flink.v1_7.table.plan.nodes.logical.FlinkLogicalRel;
import org.apache.calcite.plan.Convention;

/**
 * @author quxiucheng
 * @date 2019-02-21 17:03:00
 */
public class FlinkConventions {
    public static Convention LOGICAL = new Convention.Impl("LOGICAL", FlinkLogicalRel.class);

    public static Convention DATASTREAM = new Convention.Impl("DATASTREAM", DataStreamRel.class);
}
