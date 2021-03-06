# The OpenTRV project licenses this file to you
# under the Apache Licence, Version 2.0 (the "Licence");
# you may not use this file except in compliance
# with the Licence. You may obtain a copy of the Licence at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the Licence is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied. See the Licence for the
# specific language governing permissions and limitations
# under the Licence.
#
# Author(s) / Copyright (s): Bruno Girin 2016

import unittest
import datetime

import opentrv.data

class TestRecord(unittest.TestCase):
    def test_init(self):
        ts = datetime.datetime.utcnow()
        r = opentrv.data.Record("t", ts, 10)
        self.assertEqual("t", r.name)
        self.assertEqual(ts, r.timestamp)
        self.assertEqual(10, r.value)
        self.assertIsNone(r.unit)
        self.assertIsNone(r.topic)

    def test_str_basic(self):
        ts = datetime.datetime.now()
        ts_str = str(int((ts - datetime.datetime.utcfromtimestamp(0)).total_seconds()))
        r = opentrv.data.Record("t", ts, 10)
        self.assertEqual("[] t@"+ts_str+" 10", str(r))

    def test_str_full(self):
        ts = datetime.datetime.now()
        ts_str = str(int((ts - datetime.datetime.utcfromtimestamp(0)).total_seconds()))
        r = opentrv.data.Record("t", ts, 10, "W", opentrv.data.Topic("topic"))
        self.assertEqual("[topic] t@"+ts_str+" 10 W", str(r))

    def test_str_nested_topic(self):
        ts = datetime.datetime.now()
        ts_str = str(int((ts - datetime.datetime.utcfromtimestamp(0)).total_seconds()))
        r = opentrv.data.Record(
            "t", ts, 10, "W", opentrv.data.Topic(
                "topic", opentrv.data.Topic("my")))
        self.assertEqual("[my/topic] t@"+ts_str+" 10 W", str(r))

class TestTopic(unittest.TestCase):
    def test_init(self):
        t = opentrv.data.Topic("mytopic")
        self.assertEqual("mytopic", t.name)

    def test_init_complex_name(self):
        t = opentrv.data.Topic("my/topic")
        self.assertEqual("topic", t.name)
        self.assertIsNotNone(t.parent)
        self.assertEqual("my", t.parent.name)

    def test_init_complex_name_prefix(self):
        t = opentrv.data.Topic("//my/topic")
        self.assertEqual("topic", t.name)
        self.assertIsNotNone(t.parent)
        self.assertEqual("my", t.parent.name)

    def test_init_complex_name_suffix(self):
        t = opentrv.data.Topic("my/topic//")
        self.assertEqual("topic", t.name)
        self.assertIsNotNone(t.parent)
        self.assertEqual("my", t.parent.name)

    def test_init_complex_name_double_sep(self):
        t = opentrv.data.Topic("my//topic")
        self.assertEqual("topic", t.name)
        self.assertIsNotNone(t.parent)
        self.assertEqual("my", t.parent.name)

    def test_init_complex_name_custom_sep(self):
        t = opentrv.data.Topic("my.topic", sep='.')
        self.assertEqual("topic", t.name)
        self.assertIsNotNone(t.parent)
        self.assertEqual("my", t.parent.name)

    def test_init_complex_name_with_parent(self):
        t = opentrv.data.Topic("my/topic", opentrv.data.Topic("oh"))
        self.assertEqual("topic", t.name)
        self.assertIsNotNone(t.parent)
        self.assertEqual("my", t.parent.name)
        self.assertIsNotNone(t.parent.parent)
        self.assertEqual("oh", t.parent.parent.name)

    def test_path_single(self):
        t = opentrv.data.Topic("mytopic")
        self.assertEqual("mytopic", t.path())

    def test_path_multiple(self):
        t1 = opentrv.data.Topic("one")
        t2 = opentrv.data.Topic("two", t1)
        self.assertEqual("one/two", t2.path())

    def test_path_multiple_sep(self):
        t1 = opentrv.data.Topic("one")
        t2 = opentrv.data.Topic("two", t1)
        self.assertEqual("one.two", t2.path('.'))

    def test_eq(self):
        t1 = opentrv.data.Topic("topic")
        t2 = opentrv.data.Topic("topic")
        self.assertEqual(t1, t2)

    def test_eq_with_parent(self):
        t1 = opentrv.data.Topic("topic", opentrv.data.Topic("parent"))
        t2 = opentrv.data.Topic("topic", opentrv.data.Topic("parent"))
        self.assertEqual(t1, t2)

    def test_ne(self):
        t1 = opentrv.data.Topic("topic")
        t2 = opentrv.data.Topic("other")
        self.assertNotEqual(t1, t2)

    def test_ne_with_parent(self):
        t1 = opentrv.data.Topic("topic", opentrv.data.Topic("parent"))
        t2 = opentrv.data.Topic("topic", opentrv.data.Topic("other"))
        self.assertNotEqual(t1, t2)

    def test_ne_with_one_parent_other(self):
        t1 = opentrv.data.Topic("topic", opentrv.data.Topic("parent"))
        t2 = opentrv.data.Topic("topic")
        self.assertNotEqual(t1, t2)

    def test_ne_with_one_parent_self(self):
        t1 = opentrv.data.Topic("topic")
        t2 = opentrv.data.Topic("topic", opentrv.data.Topic("parent"))
        self.assertNotEqual(t1, t2)

    def test_str(self):
        t = opentrv.data.Topic("topic", opentrv.data.Topic("parent"))
        self.assertEqual(t.path(), str(t))

    def test_as_list(self):
        t = opentrv.data.Topic("my/long/topic")
        l = t.as_list()
        self.assertListEqual(["my","long","topic"], l)

    def test_relative_to(self):
        t = opentrv.data.Topic("my/long/topic")
        r = opentrv.data.Topic("my/long")
        self.assertEqual("topic", t.relative_to(r).path())

if __name__ == '__main__':
    unittest.main()