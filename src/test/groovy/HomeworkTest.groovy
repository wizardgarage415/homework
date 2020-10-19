import spock.lang.Specification

class HomeworkTest extends Specification {

    def homework = new homework()

    def "33% percentage to margin"() {
        given:
        def percentageStr = "33%"

        when:
        def margin = homework.getMarginValue(percentageStr)

        then:
        margin == 0.33
    }

    def "0.8 percentage to margin"() {
        given:
        def percentageStr = "0.8"

        when:
        def margin = homework.getMarginValue(percentageStr)

        then:
        margin == 0.8
    }

    def "invalid 133% percentage to margin"() {
        given:
        def percentageStr = "133%"

        when:
        def margin = homework.getMarginValue(percentageStr)

        then:
        thrown(NumberFormatException)
    }

    def "invalid 3X3y percentage to margin"() {
        given:
        def percentageStr = "3X3y"

        when:
        def margin = homework.getMarginValue(percentageStr)

        then:
        thrown(NumberFormatException)
    }

    def "calculate price for cost 25 and margin 0.2"() {
        given:
        def cost = 25
        def margin = 0.2

        when:
        def price = homework.calculatePrice(cost, margin)

        then:
        price == 30
    }


}
