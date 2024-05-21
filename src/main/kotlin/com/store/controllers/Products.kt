package com.store.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/products")
class ProductsController {

    private val productsMap = mutableMapOf<Int, Product>()

    @GetMapping
    fun getProducts(@RequestParam(required = false) type: String?): ResponseEntity<Any> {
        if (type != null && !isValidProductType(type)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(mapOf("error" to "Invalid product type"))
        }

        val filteredProducts = if (type != null) {
            productsMap.values.filter { it.type == type }
        } else {
            productsMap.values.toList()
        }
        return ResponseEntity.ok(filteredProducts)
    }

    @PostMapping
    fun createProduct(@RequestBody productDetails: ProductDetails): ResponseEntity<Any> {
        // Check for null fields in the request body
        if (productDetails.name == null || productDetails.type == null || 
            productDetails.inventory == null || productDetails.cost == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(mapOf("error" to "Invalid product data: null value not allowed"))
        }

        // Validate the product data
        if (!isValidProductData(productDetails)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(mapOf("error" to "Invalid product data"))
        }

        // Process the product creation
        val productId = productsMap.size + 1
        val product = Product(
            id = productId,
            name = productDetails.name,
            type = productDetails.type,
            inventory = productDetails.inventory,
            cost = productDetails.cost
        )
        productsMap[productId] = product
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductId(productId))
    }

    private fun isValidProductData(productDetails: ProductDetails): Boolean {
        return isValidProductName(productDetails.name) &&
                isValidProductType(productDetails.type) &&
                isValidInventory(productDetails.inventory) &&
                isValidCost(productDetails.cost)
    }

    // Function to validate the product name as string
    private fun isValidProductName(name: String?): Boolean {
        return !name.isNullOrBlank() &&
                name != "true" &&
                name != "false" &&
                name.toIntOrNull() == null
    }

    // Function to validate the product type
    private fun isValidProductType(type: String?): Boolean {
        val validTypes = setOf("book", "food", "gadget", "other")
        return type in validTypes
    }

    // Function to validate the product inventory
    private fun isValidInventory(inventory: Number?): Boolean {
        return inventory != null && inventory.toDouble() >= 0
    }

    // Function to validate the product cost
    private fun isValidCost(cost: Double?): Boolean {
        return cost != null && cost >= 0.0
    }
}

data class Product(val id: Int, val name: String, val type: String, val inventory: Number, val cost: Double)

data class ProductDetails(val name: String?, val type: String?, val inventory: Number?, val cost: Double?)

data class ProductId(val id: Int)
