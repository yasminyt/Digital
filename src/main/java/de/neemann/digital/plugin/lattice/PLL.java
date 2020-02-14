package de.neemann.digital.plugin.lattice;

import de.neemann.digital.core.Node;
import de.neemann.digital.core.NodeException;
import de.neemann.digital.core.ObservableValue;
import de.neemann.digital.core.ObservableValues;
import de.neemann.digital.core.element.Element;
import de.neemann.digital.core.element.ElementAttributes;
import de.neemann.digital.core.element.ElementTypeDescription;
import de.neemann.digital.core.element.Keys;
import de.neemann.digital.draw.elements.PinException;

import static de.neemann.digital.core.element.PinInfo.input;

/**
 * A PLL component in Lattice
 */
public class PLL extends Node implements Element {

    /**
     * The PLL description
     */
    public static final ElementTypeDescription DESCRIPTION = new ElementTypeDescription(PLL.class,
            input("CLKI", ""))
            .addAttribute(Keys.ROTATE)
            .addAttribute(Keys.LABEL);

    private final String label;

    private ObservableValue clkI;

    private ObservableValue clkOp;

    private long outValue;

    public PLL(ElementAttributes attributes) {
        label = attributes.getLabel();
        clkOp = new ObservableValue("CLKOP", 1).setPinDescription(DESCRIPTION);
    }

    @Override
    public void readInputs() throws NodeException {
        this.outValue = this.clkI.getValue();
    }

    @Override
    public void writeOutputs() throws NodeException {
        clkOp.setValue(this.outValue);
    }

    @Override
    public void setInputs(ObservableValues inputs) throws NodeException {
        this.clkI = inputs.get(0).addObserverToValue(this).checkBits(1, this);
    }

    @Override
    public ObservableValues getOutputs() throws PinException {
        return this.clkOp.asList();
    }

    public String getLabel() {
        return label;
    }
}
