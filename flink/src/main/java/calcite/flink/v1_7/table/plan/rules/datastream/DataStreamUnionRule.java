package calcite.flink.v1_7.table.plan.rules.datastream;

import calcite.flink.v1_7.table.plan.FlinkConventions;
import calcite.flink.v1_7.table.plan.nodes.datastream.DataStreamUnion;
import calcite.flink.v1_7.table.plan.nodes.logical.FlinkLogicalUnion;
import calcite.flink.v1_7.table.schema.RowSchema;
import lombok.val;
import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.plan.RelTrait;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.convert.ConverterRule;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author quxiucheng
 * @date 2019-02-21 17:39:00
 */
public class DataStreamUnionRule extends ConverterRule {

    public static RelOptRule INSTANCE = new DataStreamUnionRule();


    public DataStreamUnionRule() {
        super(FlinkLogicalUnion.class, FlinkConventions.LOGICAL, FlinkConventions.DATASTREAM, "DataStreamUnionRule");

    }

    @Override
    public RelNode convert(RelNode rel) {
        FlinkLogicalUnion union = (FlinkLogicalUnion) rel;
        RelTraitSet traitSet = rel.getTraitSet().replace(FlinkConventions.DATASTREAM);
        List<RelNode> newInputs = union.getInputs().stream().map(u -> RelOptRule.convert(u, FlinkConventions.DATASTREAM)).collect(Collectors.toList());

        return new DataStreamUnion(
                rel.getCluster(),
                traitSet,
                newInputs,
                new RowSchema(rel.getRowType()));
    }
}
