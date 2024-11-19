    const nodes = [
    { id: '양산 이관', x: 50, y: 50 },
    { id: '생산 계획', x: 50, y: 150 },
    { id: '조달 계획 수립', x: 200, y: 100 },
    { id: '품목 정보 등록', x: 400, y: 50 },
    { id: '견적입수', x: 400, y: 150 },
    { id: '거래 협상', x: 600, y: 100 },
    { id: '거래 계약', x: 600, y: 200 },
    { id: '계약 등록', x: 750, y: 200 },
    { id: '조달 계획 등록', x: 600, y: 300 },
    { id: '구매 BOM', x: 900, y: 50 },
    { id: '조달 계획서', x: 900, y: 100 },
    { id: '견적서', x: 900, y: 150 },
    { id: '계약서', x: 900, y: 200 },
    { id: '조달 계획', x: 900, y: 300 }
    ];

    const links = [
    { source: '양산 이관', target: '품목 정보 등록' },
    { source: '생산 계획', target: '조달 계획 수립' },
    { source: '조달 계획 수립', target: '품목 정보 등록' },
    { source: '품목 정보 등록', target: '견적입수' },
    { source: '견적입수', target: '거래 협상' },
    { source: '거래 협상', target: '거래 계약' },
    { source: '거래 계약', target: '계약 등록' },
    { source: '계약 등록', target: '조달 계획 등록' },
    { source: '품목 정보 등록', target: '구매 BOM' },
    { source: '조달 계획 수립', target: '조달 계획서' },
    { source: '견적입수', target: '견적서' },
    { source: '계약 등록', target: '계약서' },
    { source: '조달 계획 등록', target: '조달 계획' }
    ];

    const svg = d3.select("#diagram");

    svg.selectAll("line")
    .data(links)
    .enter()
    .append("line")
    .attr("x1", d => nodes.find(n => n.id === d.source).x + 80)
    .attr("y1", d => nodes.find(n => n.id === d.source).y + 15)
    .attr("x2", d => nodes.find(n => n.id === d.target).x)
    .attr("y2", d => nodes.find(n => n.id === d.target).y + 15)
    .attr("class", "link")
    .attr("marker-end", "url(#arrow)");

    svg.append("defs").append("marker")
    .attr("id", "arrow")
    .attr("viewBox", "0 -5 10 10")
    .attr("refX", 10)
    .attr("refY", 0)
    .attr("markerWidth", 6)
    .attr("markerHeight", 6)
    .attr("orient", "auto")
    .append("path")
    .attr("d", "M0,-5L10,0L0,5")
    .attr("fill", "#4a6fa1");

    // Node click event
    svg.selectAll("rect")
        .data(nodes)
        .enter()
        .append("rect")
        .attr("class", "node")
        .attr("x", d => d.x)
        .attr("y", d => d.y)
        .attr("width", 120)
        .attr("height", 30)
        .on("click", function(event, d) {
            const rect = d3.select(this);
            if (rect.classed("complete")) {
                rect.classed("complete", false).classed("in-progress", true);
            } else if (rect.classed("in-progress")) {
                rect.classed("in-progress", false);
            } else {
                rect.classed("complete", true);
            }
        });


    svg.selectAll("text")
    .data(nodes)
    .enter()
    .append("text")
    .attr("x", d => d.x + 60)
    .attr("y", d => d.y + 15)
    .text(d => d.id);
