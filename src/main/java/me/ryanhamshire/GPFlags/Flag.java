package me.ryanhamshire.GPFlags;

import me.ryanhamshire.GPFlags.flags.FlagDefinition;

/**
 * Main flag object
 */
public class Flag {

    FlagDefinition flagDefinition;
    public String parameters;
    private String[] parametersArray;
    private boolean set = true;

    Flag(FlagDefinition definition, String parameters) {
        this.flagDefinition = definition;
        this.parameters = parameters;
        this.parameters = this.parameters.replace('$', (char) 0x00A7);
        this.parameters = this.parameters.replace('&', (char) 0x00A7);
    }

    /**
     * Get parameters for this flag
     *
     * @return Parameters for this flag
     */
    public String[] getParametersArray() {
        if (this.parametersArray != null) return this.parametersArray;
        this.parametersArray = this.parameters.split(" ");
        return this.parametersArray;
    }

    /**
     * Gets whether or not this flag is currently active
     *
     * @return Activity of flag
     */
    public boolean getSet() {
        return this.set;
    }

    /**
     * Sets whether or not this flag is currently active
     *
     * @param value Wether or not the flag is active
     */
    public void setSet(boolean value) {
        this.set = value;
    }

    /**
     * Get the flag definition of this flag
     *
     * @return Flag definition of this flag
     */
    public FlagDefinition getFlagDefinition() {
        return flagDefinition;
    }

}
