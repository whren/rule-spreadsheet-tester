package stest

import junit.framework._
import Assert._
import org.drools.io.ResourceFactory
import org.drools.{KnowledgeBaseFactory, KnowledgeBase}
import org.mvel2.MVEL
import java.util.{HashMap => JavaHash}
import jxl.{Sheet, Workbook, Cell}
import scala.xml.XML
import org.drools.builder._

/**
 * Remember for all test runners to work, must return Unit !
 * Some of them won't work otherwise (eg surefire !).
 * @author Michael Neale
 */
class RunnerTest extends TestCase {

     def testWholeWorkbook = {


        val kb = KnowledgeBuilderFactory.newKnowledgeBuilder
        kb.add(ResourceFactory.newInputStreamResource(getClass getResourceAsStream("myrules.drl")), ResourceType.DRL)
        assertFalse(kb.hasErrors)
        val kbase = KnowledgeBaseFactory.newKnowledgeBase
        kbase.addKnowledgePackages(kb.getKnowledgePackages)

        val rt = new Runner(kbase)
        val result = rt.runTestsInWorkbook(getClass getResourceAsStream("TestWorkbook.xls"))
        assertEquals(3, result.size)

        assertNotNull(result(0).sheetName)
        assertNotNull(result(0).scenarioReports)
        assertTrue(result(0).scenarioReports.length > 0)
        println(result(0))

      }

      def testWorkbookLoad = {
        val st = getClass getResourceAsStream("TestWorkbook.xls")
        assertNotNull(st)
        val w = Workbook.getWorkbook (st)
        assertNotNull(w)

        val rt = new Runner(KnowledgeBaseFactory.newKnowledgeBase)
        val reports = rt.processSheet(w.getSheets()(0))

        val rep1 = reports(0)
        println(rep1.name)
        assertEquals(0, rep1.failures.size)
        assertEquals("This is a scenario", rep1.name)
        assertEquals(4, rep1.totalTests)
        println("Failures : " + rep1.failures.size)
        println("Total Tests : " + rep1.totalTests)

        val rep2 = reports(1)
        println(rep2.name)
        assertEquals(2, rep2.failures.size)
        println(rep2.failures(0))
      }


      def testRulesLoading  = {
        val kb = KnowledgeBuilderFactory.newKnowledgeBuilder
        kb.add(ResourceFactory.newInputStreamResource(getClass getResourceAsStream("myrules.drl")), ResourceType.DRL)
        val pkgs = kb.getKnowledgePackages
        assertFalse(pkgs.isEmpty)
        val kbase = KnowledgeBaseFactory.newKnowledgeBase
        kbase.addKnowledgePackages(pkgs)
        assertNotNull(kbase)

        val session = kbase.newStatelessKnowledgeSession
        assertNotNull (session)
        println("OK")

        println("hey")
      }

      def testWithRules = {
        val w = Workbook.getWorkbook (getClass getResourceAsStream("TestWorkbook.xls"))

        val kb = KnowledgeBuilderFactory.newKnowledgeBuilder
        kb.add(ResourceFactory.newInputStreamResource(getClass getResourceAsStream("myrules.drl")), ResourceType.DRL)
        assertFalse(kb.hasErrors)
        val kbase = KnowledgeBaseFactory.newKnowledgeBase
        kbase.addKnowledgePackages(kb.getKnowledgePackages)

        val rt = new Runner(kbase)
        val reports = rt.processSheet(w.getSheets()(0))

        assertNotNull(reports(0).name)
        assertNotNull(reports(1).name)

        assertEquals(0, rt.processSheet(w.getSheets()(1)).length)

        


      }




    

    def testMVEL : Unit = {

        val f = new SampleFact
        val hm = new JavaHash[String, Any]
        hm.put("f",  f)
        assertNotNull(MVEL.eval("f.name='mic'", hm))
        assertNotNull(MVEL.eval("f.age='42'", hm))
        assertEquals("mic", f.name)
        assertEquals(42, f.age)
        assertTrue(MVEL.eval("f.age== '42'", hm).asInstanceOf[Boolean])

        println("done")

    }


    def testAtom : Unit = {
        val feed = <feed xmlns="http://www.w3.org/2005/Atom">
                 <title>Example Feed</title>
                 <subtitle>A subtitle.</subtitle>
                 <link href="http://example.org/feed/" rel="self"/>
                 <link href="http://example.org/"/>
                 <updated>2003-12-13T18:30:02Z</updated>
                 <author>
                   <name>John Doe</name>
                   <email>johndoe@example.com</email>
                 </author>
                        { List(1, 3, 4, 5).map(i => entryItems(i))  }
                 <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>
                </feed>

        
        println(feed)

    }

    def entryItems(i: Int) : xml.Elem = {
            <entry>
              <title>Atom-Powered Robots Run Amok</title>
              <link href="http://example.org/2003/12/13/atom03"/>
              <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>
              <updated>2003-12-13T18:30:02Z</updated>
              <summary>{ i }</summary>
            </entry>

        
    }





}
