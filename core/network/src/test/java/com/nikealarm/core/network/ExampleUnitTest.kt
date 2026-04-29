package com.nikealarm.core.network

import com.google.gson.Gson
import com.nikealarm.core.network.model.product.ProductDTO
import org.junit.Test

import org.junit.Assert.*
import java.io.InputStreamReader

class ExampleUnitTest {

    private val gson = Gson()

    @Test
    fun jsonParsingTest() {
        val inputStream = javaClass.classLoader?.getResourceAsStream("sample_response.json")
        val reader = InputStreamReader(inputStream!!)

        // When (실행): GSON을 이용해 JSON을 통째로 우리가 만든 DTO로 변환합니다.
        val parsedResponse = gson.fromJson(reader, ProductDTO::class.java)

        // Then (검증): 파싱된 데이터가 의도한 좌표에 잘 꽂혔는지 확인합니다.
        assertNotNull("파싱 결과가 null입니다!", parsedResponse)

        // 첫 번째 상품 객체 하나를 꺼내서 확인
        val firstItem = parsedResponse.objects.firstOrNull()
        assertNotNull("objects 배열이 비어있습니다!", firstItem)

        println("✅ 파싱된 상품명: $firstItem")
    }
}