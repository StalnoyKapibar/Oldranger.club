window.onload = function () {
    const flatData = [
        { "name": "root", "parent": "" },
        { "name": "Boss", "parent": "root" },
        { "name": "User 3", "parent": "root" },
        { "name": "User 4", "parent": "root" },
        { "name": "User 1", "parent": "Boss" },
        { "name": "User 2", "parent": "Boss" },
        { "name": "User 21", "parent": "User 2" },
        { "name": "User 22", "parent": "User 2" },
        { "name": "User 31", "parent": "User 3" },
        { "name": "User 32", "parent": "User 3" },
        { "name": "User 41", "parent": "User 4" },
        { "name": "User 411", "parent": "User 41" },
        { "name": "User 4111", "parent": "User 411" },
        { "name": "User 41111", "parent": "User 4111" },
        { "name": "User 5", "parent": "root" },
    ];
    treeSimple(flatData);
}

const treeSimple = function (flatData) {

    // convert the flat data into a hierarchy
    let treeData = d3.stratify().id(el => el.name).parentId(el => el.parent)(flatData);

    // assign the name to each node
    treeData.each(el => el.name = el.data.name);

    // Creates a curved (diagonal) path from parent to the child nodes
    const nodeCurvedPath = (s, d) => {
        return `M ${s.y} ${s.x} C ${(s.y + d.y) / 2} ${s.x}, ${(s.y + d.y) / 2} ${d.x}, ${d.y} ${d.x}`;
    }

    // Collapse the node and all it's children
    const nodeCollapse = el => {
        if (d.children) {
            d._children = d.children;
            d._children.forEach(nodeCollapse);
            d.children = null;
        }
    }

    // Set the dimensions and margins of the diagram
    const margin = {top: 20, right: 90, bottom: 30, left: 90},
        width = 960 - margin.left - margin.right,
        height = 500 - margin.top - margin.bottom;

    // append the svg object to the body of the page
    // appends a 'group' element to 'svg'
    // moves the 'group' element to the top left margin
    const svg = d3.select('body').append('svg')
        .attr('width', width + margin.right + margin.left)
        .attr('height', height + margin.top + margin.bottom)
        .append('g')
        .attr('transform', `translate(${margin.left},${margin.top})`);

    let i = 0, duration = 500;

    // declares a tree layout and assigns the size
    const treemap = d3.tree().size([height, width]);

    // Assigns the data to a hierarchy using parent-child relationships
    let inputNodeData = d3.hierarchy(treeData, el => el.children);

    //	Initialize so nodes appear before links/diagonals on display event
    // Assigns parent, children, height, depth
    //
    inputNodeData.x0 = height / 2;
    inputNodeData.y0 = 0;

    // Collapse after the second level
    //root.children.forEach( nodeCollapse );

    const treeUpdate = function (source) {

        // 	Assigns the x and y position for the nodes maps the node data to the tree layout
        let inputTreeData = treemap(inputNodeData);

        // 	Compute the new tree layout.
        let inputNodes = inputTreeData.descendants(),
            inputLinks = inputTreeData.descendants().slice(1);

        // 	Normalize for fixed-depth.
        inputNodes.forEach(d => d.y = d.depth * 120);

        // 	Update the nodes...
        let inputNode = svg.selectAll('g.node')
            .data(inputNodes, d => d.id || (d.id = ++i));

        // 	Enter any new nodes at the parent's previous position.
        let nodeEnter = inputNode.enter().append('g')
            .attr('class', 'node')
            .attr('transform', d => `translate(${source.y0},${source.x0})`)
            .on('click', treeToggleChildren);

        //	Add Circle for the nodes
        nodeEnter.append('circle')
            .attr('class', 'node')
            .attr('r', 1e-6)
            .style('fill', d => d._children ? "lightsteelblue" : "#fff");

        //	Add labels for the nodes
        nodeEnter.append('text')
            .attr('dy', '.35em')
            .attr('x', d => d.children || d._children ? -13 : 13)
            .attr('text-anchor', d => d.children || d._children ? 'end' : 'start')
            .text(d => d.data.name);

        //	UPDATE
        let nodeUpdate = nodeEnter.merge(inputNode);

        //	Transition to the proper position for the node
        nodeUpdate.transition()
            .duration(duration)
            .attr('transform', d => `translate(${d.y},${d.x})`);

        //	Update the node attributes and style
        nodeUpdate.select('circle.node')
            .attr('r', 10)
            .style('fill', d => d._children ? "lightsteelblue" : "#fff")
            .attr('cursor', 'pointer');

        //	Remove any exiting nodes
        let nodeExit = inputNode.exit().transition()
            .duration(duration)
            .attr('transform', d => `translate(${source.y},${source.x})`)
            .remove();

        //	On exit reduce the node circles size to 0
        nodeExit.select('circle')
            .attr('r', 1e-6);

        //	On exit reduce the opacity of text labels
        nodeExit.select('text')
            .style('fill-opacity', 1e-6);

        //
        //	END TREE - UPDATE - NODES

        //	Update the links...
        let inputLink = svg.selectAll('path.link')
            .data(inputLinks, d => d.id);

        //	Enter any new links at the parent's previous position.
        let linkEnter = inputLink.enter().insert('path', 'g')
            .attr('class', 'link')
            .attr('d', () => nodeCurvedPath({ x: source.x0, y: source.y0 }, { x: source.x0, y: source.y0 }));

        //	UPDATE
        let linkUpdate = linkEnter.merge(inputLink);

        //	Transition back to the parent element position
        linkUpdate.transition()
            .duration(duration)
            .attr('d', d => nodeCurvedPath(d, d.parent));

        //	Remove any exiting links
        let linkExit = inputLink.exit().transition()
            .duration(duration)
            .attr('d', () => nodeCurvedPath({ x: source.x, y: source.y }, { x: source.x, y: source.y }))
            .remove();
        //
        //	END TREE - UPDATE - LINKS

        //	Store the old positions for transition.
        inputNodes.forEach(d => {
            d.x0 = d.x;
            d.y0 = d.y;
        });

        // Toggle children on click.
        function treeToggleChildren(d) {
            if (d.children) {
                d._children = d.children;
                d.children = null;
            } else {
                d.children = d._children;
                d._children = null;
            }
            treeUpdate(d);
        }
    }
    treeUpdate(inputNodeData);
}
