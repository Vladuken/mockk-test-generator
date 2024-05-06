package com.vladuken.plugin.mockkgenerator.parser

import com.vladuken.plugin.mockkgenerator.model.DomainClassInfo
import com.vladuken.plugin.mockkgenerator.model.DomainConstructorParameters
import org.jetbrains.kotlin.idea.base.psi.kotlinFqName
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.nj2k.types.typeFqName
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFunctionType
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.KtUserType


/**
 * Prepare [DomainClassInfo] representation from [KtClass] instance.
 */
fun KtClass.parseToDomainClassInfo(): DomainClassInfo {
    return DomainClassInfo(
        packageName = containingKtFile.packageFqName.toString(),
        className = name.orEmpty(),
        fqClassName = kotlinFqName.toString(),
        parameters = parseToDomainConstructorParameters(),
    )
}

private fun KtClass.parseToDomainConstructorParameters(): List<DomainConstructorParameters> {
    /**
     * Return FQN name for [KtTypeReference]
     */
    fun KtTypeReference.asFqNameList(): List<String> {
        return when (val type = typeElement) {
            is KtUserType -> {
                // Resolve and extract FQN from outer type
                val currentTypeFqName = type.referenceExpression?.mainReference?.resolve()?.kotlinFqName?.toString()
                // Get inner types FQNs
                val innerTypesFqNames = type.typeArguments.flatMap { it.typeReference?.asFqNameList() ?: emptyList() }
                (innerTypesFqNames + currentTypeFqName).filterNotNull()
            }

            is KtFunctionType -> type.typeArgumentsAsTypes.flatMap { it.asFqNameList() }
            else -> emptyList()
        }
    }

    val items = primaryConstructor
        ?.valueParameters
        ?.map { ktParameter ->
            DomainConstructorParameters(
                parameterTypeFqn = ktParameter.typeFqName().toString(),
                parameterName = ktParameter.name.toString(),
                rawTypeString = ktParameter.typeReference?.text.toString(),
                nestedGenericTypeName = when (val typeElement = ktParameter.typeReference?.typeElement) {
                    is KtUserType -> typeElement.typeArguments.firstOrNull()?.text.orEmpty()
                    is KtFunctionType -> typeElement.text
                    else -> ""
                },

                nestedGenericTypesFqn = ktParameter.typeReference?.asFqNameList() ?: emptyList()
            )
        }

    return items ?: emptyList()
}