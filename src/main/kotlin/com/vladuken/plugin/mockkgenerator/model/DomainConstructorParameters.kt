package com.vladuken.plugin.mockkgenerator.model

/**
 * Representation of constructor parameter for class that used for test generation.
 */
data class DomainConstructorParameters(
    /**
     * Parameter type FQN string.
     */
    val parameterTypeFqn: String,
    /**
     * Declared name of parameter.
     */
    val parameterName: String,
    /**
     * String that represents type of parameter as it was written in constructor.
     *
     * Example:
     * <code>
     *
     *     class A(
     *          val typeName : Pair<Int,String>
     *     )
     * <code>
     *
     * Will result in "Pair<Int,String>" value
     *
     */
    val rawTypeString: String,
    /**
     * String that represents inner type of generic typed parameter.
     *
     * Example:
     * <code>
     *
     *     class A(
     *          val typeName : List<String>
     *     )
     * <code>
     *
     * Will result in "String" value
     *
     */
    val nestedGenericTypeName: String,
    /**
     * Return all inner type FQNs that used for generic.
     */
    val nestedGenericTypesFqn: List<String>,
)

