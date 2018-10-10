//得到iframe里面body元素下的所有的css类名并放入数组返回
function getIframeClassNames() {
    var attributes = [];
    var iframeEle = document.getElementById('iframe');
    var doc = iframeEle.contentDocument || iframeEle.contentWindow.document;
    $(doc).find('body *[zdwid]').each(function (i, node) {
        var className = $(doc).find(node).attr('class');
        if (className){
            var classArr = className.split(' ');
            var len=classArr.length;
            for (var j = 0; j < len; j++){
                var attr = classArr[j].trim();
                if (attr && $.inArray(attr, attributes) == -1){
                    attributes.push(attr);
                }
            }
        }
    });
    return attributes;
}

// 得到一个元素的绝对路径选择器
//body > div:first-child > div#div1
//body > div:first-child
//body
//body > div:first-child > div.divclass.divclass2:nth-child(2)
function getElementSelector(element, domAttrs){
    var paths = [];
    var _document = element.ownerDocument;
    for (;; element=element.parentElement){
        var tagName = element.tagName.toLowerCase();
        var preSiblingsCount = $(element).prevAll().length;
        //点击元素时， 可能会有动态class类被添加
        var classValue = filterClassValue(element.className.trim(), domAttrs);
        if (element && element.id){
            paths.push(tagName + '#'+ element.id);
        } else {
            var nthLocate = preSiblingsCount? ':nth-child(' + (preSiblingsCount + 1) + ')': ':first-child';
            if (tagName === 'body'){
                paths.push(tagName);
                break;
            }
            if (classValue){
                var classCss = classValue.split(' ').join('.');
                var onlyClassSelector = tagName + '.' + classCss;
                var classSelectorWithChild = classCss? ('.' + classCss): '';
                var thisNodeCount = $(_document).find(onlyClassSelector).length;
                if (thisNodeCount === 1){
                    paths.push(onlyClassSelector);
                } else {
                    paths.push(tagName  + classSelectorWithChild + nthLocate);
                }
            } else {
                var tagCssSelector = tagName + nthLocate;
                paths.push(tagCssSelector);
            }
        }
    }
    paths.reverse();
    return paths.join(' > ');//console.log(paths.join(' > '));
}

//过滤掉不在数组domAttrs中的class
function filterClassValue(classValue, domAttrs) {
    var resultClass = [];
    var classList = classValue.split(' ');
    var len=classList.length;
    for (var i = 0; i < len; i++){
        var c_v = classList[i].trim();
        if (c_v && $.inArray(c_v, domAttrs) !== -1) {
            resultClass.push(c_v);
        }
    }
    return resultClass.join(' ');
}
//比较两个绝对路径的css选择器
function diffArray(x, y) {
    var max= Math.max(x.length, y.length);

    rs = [];
    for (var i=0; i<max; i++) {
        if (x[i] != y[i]) {
            rs.push(i);
        }
    }
    return rs;
}

var getDiffOne = function(items) {
    var a1 = items[0],
        a2 = items[1],
        a3 = items[2];

    if (a1[1] == a2[1] && a2[1] == a3[1]) {
        $.each(items.slice(3), function(i, item) {
            if (!a1[1] == item[1]) {
                return item;
            }
        });
    } else if (a1[1] == a2[1]) {
        return a3;
    } else if (a1[1] == a3[1]) {
        return a2;
    }
    return a1;
};


var filterMostItem = function(store) {
    var frequency = {};  // array of frequency.
    var max = 0;  // holds the max frequency.
    var result;   // holds the max frequency element.
    store.forEach(function(item) {
        frequency[item[1]]=(frequency[item[1]] || 0)+1; // increment frequency.
        if(frequency[item[1]] > max) { // is this frequency > max so far ?
            max = frequency[item[1]];  // update max.
            result = item[1];          // update result.
        }
    });
    return result;
};