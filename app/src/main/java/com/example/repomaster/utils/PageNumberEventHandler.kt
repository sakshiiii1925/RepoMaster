package com.example.repomaster.utils

import com.itextpdf.kernel.events.Event
import com.itextpdf.kernel.events.IEventHandler
import com.itextpdf.kernel.events.PdfDocumentEvent
import com.itextpdf.kernel.geom.Rectangle
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import com.itextpdf.layout.Canvas
import com.itextpdf.layout.properties.TextAlignment

class PageNumberEventHandler : IEventHandler {

    override fun handleEvent(event: Event) {

        val docEvent = event as PdfDocumentEvent

        val pdf = docEvent.document

        val page = docEvent.page

        val pageNumber = pdf.getPageNumber(page)

        val pageSize: Rectangle = page.pageSize

        val pdfCanvas = PdfCanvas(
            page.newContentStreamAfter(),
            page.resources,
            pdf
        )

        val canvas = Canvas(pdfCanvas, pageSize)

        canvas.showTextAligned(
            "Page $pageNumber",
            pageSize.width / 2,
            20f,
            TextAlignment.CENTER
        )

        canvas.close()
    }
}