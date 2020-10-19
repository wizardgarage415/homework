import spock.lang.Shared
import spock.lang.Specification

class CategoryTreeTest extends Specification {

    @Shared def categoryMap = [
        "C1" : new MarginCategory(name: "C1", margin: 0.2, costRangeFrom: 0, costRangeTo: 25),
        "C2" : new MarginCategory(name: "C2", margin: 0.3, costRangeFrom: 25, costRangeTo: 50),
        "C3" : new MarginCategory(name: "C3", margin: 0.4, costRangeFrom: 50, costRangeTo: 75),
        "C4" : new MarginCategory(name: "C4", margin: 0.5, costRangeFrom: 75, costRangeTo: 100),
        "C5" : new MarginCategory(name: "C5", margin: 0.6, costRangeFrom: 100, costRangeTo: null),
    ]

    def "create one node tree"() {
        given:
        def testList = [ categoryMap["C1"] ]

        when:
        def tree = CategoryTree.createTree(testList)

        then:
        getListFromTree(tree) == testList
    }

    def "create two nodes tree"() {
        given:
        def testList = [ categoryMap["C5"], categoryMap["C1"] ]
        def expectedList = [ categoryMap["C1"], categoryMap["C5"] ]

        when:
        def tree = CategoryTree.createTree(testList)

        then:
        getListFromTree(tree) == expectedList
    }

    def "create more than two nodes tree"() {
        given:
        def testList = [ categoryMap["C4"], categoryMap["C1"], categoryMap["C5"], categoryMap["C3"], categoryMap["C2"] ]
        def expectedList = [ categoryMap["C1"], categoryMap["C2"], categoryMap["C3"], categoryMap["C4"], categoryMap["C5"] ]

        when:
        def tree = CategoryTree.createTree(testList)

        then:
        getListFromTree(tree) == expectedList
    }

    def "lookup cost 0.0 - left limit C1"() {
        given:
        def tree = CategoryTree.createTree(
                new ArrayList<MarginCategory>(categoryMap.values()))

        when:
        def node = tree.lookupByMargin(0.0)

        then:
        node.margin == categoryMap["C1"].margin
    }

    def "lookup cost 50.0 - left limit C3"() {
        given:
        def tree = CategoryTree.createTree(
                new ArrayList<MarginCategory>(categoryMap.values()))

        when:
        def node = tree.lookupByMargin(50.0)

        then:
        node.margin == categoryMap["C3"].margin
    }

    def "lookup cost 121.8 - last range"() {
        given:
        def tree = CategoryTree.createTree(
                new ArrayList<MarginCategory>(categoryMap.values()))

        when:
        def node = tree.lookupByMargin(121.8)

        then:
        node.margin == categoryMap["C5"].margin
    }

    def getListFromTree(CategoryTree categoryTree) {
        if (categoryTree == null) return []
        return getListFromTree(categoryTree.leftChild) +
                categoryTree.categoryNode + getListFromTree(categoryTree.rightChild)
    }

}
