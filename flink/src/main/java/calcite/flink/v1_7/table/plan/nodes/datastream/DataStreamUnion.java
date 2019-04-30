package calcite.flink.v1_7.table.plan.nodes.datastream;

import calcite.flink.v1_7.table.schema.RowSchema;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.SetOp;
import org.apache.calcite.rel.core.Union;

import java.util.List;

/**
 * @author quxiucheng
 * @date 2019-02-21 17:47:00
 */
public class DataStreamUnion extends Union implements DataStreamRel {

    private RelOptCluster cluster;
    private RowSchema rowSchema;

    public DataStreamUnion(RelOptCluster cluster, RelTraitSet traits, List<RelNode> inputs, RowSchema rowSchema) {
        super(cluster, traits, inputs, true);
        this.rowSchema = rowSchema;
        this.cluster = cluster;
    }

    @Override
    public SetOp copy(RelTraitSet traitSet, List<RelNode> inputs, boolean all) {

       return new DataStreamUnion(
                cluster,
                traitSet,
                inputs,
                this.rowSchema);
    }
}
