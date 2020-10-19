import java.math.RoundingMode

// ---------------------------
//
// HOMEWORK
//
// Use Groovy to write a code under "YOUR CODE GOES BELOW THIS LINE" comment.
// Make sure the code is working in some of the web Groovy consoles, e.g. https://groovyconsole.appspot.com
// Do not over-engineer the solution.
//
// Assume you got some data from a customer and your task is to design a routine that will calculate the average Product price per Group.
//
// The Price of each Product is calculated as:
// Cost * (1 + Margin)
//
// Assume there can be a large number of products.
//
// Plus points:
// - use Groovy closures (wherever it makes sense)
// - make the category look-up performance effective
 
// contains information about [Product, Group, Cost]
def products = [
    ["A", "G1", 20.1],
    ["B", "G2", 98.4],
    ["C", "G1", 49.7],
    ["D", "G3", 35.8],
    ["E", "G3", 105.5],
    ["F", "G1", 55.2],
    ["G", "G1", 12.7],
    ["H", "G3", 88.6],
    ["I", "G1", 5.2],
    ["J", "G2", 72.4]]
 
// contains information about Category classification based on product Cost
// [Category, Cost range from (inclusive), Cost range to (exclusive)]
// i.e. if a Product has Cost between 0 and 25, it belongs to category C1
// ranges are mutually exclusive and the last range has a null as upper limit.
def category = [
    ["C3", 50, 75],
    ["C4", 75, 100],
    ["C2", 25, 50],
    ["C5", 100, null],
    ["C1", 0, 25]]
 
// contains information about margins for each product Category
// [Category, Margin (either percentage or absolute value)]
def margins = [
    "C1" : "20%",
    "C2" : "30%",
    "C3" : "0.4",
    "C4" : "50%",
    "C5" : "0.6"]
 
// ---------------------------
//
// YOUR CODE GOES BELOW THIS LINE
//
// Assign the 'result' variable so the assertion at the end validates
//
// ---------------------------
class MarginCategory {
    String name
    Double margin
    Double costRangeFrom
    Double costRangeTo
}

class CategoryTree {
    MarginCategory categoryNode
    CategoryTree leftChild
    CategoryTree rightChild

    static CategoryTree createTree(List<MarginCategory> categories) {
        def treeFromSortedList
        treeFromSortedList = { List<MarginCategory> categoryList ->
            def length = categoryList.size()
            if (length == 1) return new CategoryTree(categoryNode: categoryList[0])

            def center = categoryList.size().intdiv(2)
            def root = new CategoryTree(categoryNode: categoryList[center])
            root.leftChild = treeFromSortedList(categoryList.subList(0, center))
            if (length - (center + 1) > 0) {
                root.rightChild = treeFromSortedList(categoryList.subList(center + 1, length))
            }

            return root
        }

        return treeFromSortedList(categories.sort {
            cat -> cat.costRangeFrom })

    }

    MarginCategory lookupByMargin(double cost) {
        if (cost < categoryNode.costRangeFrom) {
            return leftChild == null ? null : leftChild.lookupByMargin(cost)
        } else if (categoryNode.costRangeTo != null && cost >= categoryNode.costRangeTo) {
            return rightChild == null ? null : rightChild.lookupByMargin(cost)
        }
        return categoryNode
    }
}

static double getMarginValue(marginStr) {
    if (marginStr.matches("[0-9]{1,2}%"))
        return Integer.valueOf(marginStr.substring(0, marginStr.length()-1)).doubleValue() / 100
    else
        return Double.parseDouble(marginStr)
}

static double calculatePrice(double cost, double margin) {
    return cost * ( 1 + margin)
}

def lookUpMarginByCost = {  List<List<?>> categoryList, Map<?,?> marginMap ->

    def categoryNodes = categoryList.
            collect  {cat ->
                new MarginCategory(
                        name: cat[0] as String,
                        costRangeFrom: Double.valueOf(cat[1] as String),
                        costRangeTo: cat[2] == null ? null : Double.valueOf(cat[2] as String),
                        margin: getMarginValue(marginMap[cat[0]] as String))
            }

    def lookUpMargin = { double cost, CategoryTree tree ->
        def categoryNode = tree.lookupByMargin(cost)
        return categoryNode == null ? 0.0 : categoryNode.margin
    }

    def categoryTree = CategoryTree.createTree(categoryNodes)
    return lookUpMargin.rcurry(categoryTree)

}

def priceAvgByGroup = { List<List<?>> productList, Closure<Double> getMarginFromCost ->
    def totalPriceByGroup = [:]
    def countByGroup = [:]

    productList.each { product ->
        def group = product[1] as String
        def cost = Double.parseDouble(product[2] as String)
        // increment count
        def count = countByGroup.containsKey(group) ? countByGroup[group] : 0
        countByGroup[group] = count + 1
        // sum price
        def totalPrice = totalPriceByGroup.containsKey(group) ? totalPriceByGroup[group] : 0.0
        def price = calculatePrice(cost, getMarginFromCost(cost))
        totalPriceByGroup[group] = totalPrice + price
    }

    def avgByGroup = [:]

    totalPriceByGroup.keySet().each { group ->
        def avgPrice = (totalPriceByGroup[group] as Double) / (countByGroup[group] as Integer)
        avgByGroup[group] = new BigDecimal(avgPrice).setScale(1, RoundingMode.HALF_UP).doubleValue()
    }

    return avgByGroup
}

def result = priceAvgByGroup(products, lookUpMarginByCost(category, margins))

// ---------------------------
//
// IF YOUR CODE WORKS, YOU SHOULD GET "It works!" WRITTEN IN THE CONSOLE
//
// ---------------------------
assert result == [
    "G1" : 37.5,
    "G2" : 124.5,
    "G3" : 116.1
    ] : "It doesn't work"
 
println "It works!"

