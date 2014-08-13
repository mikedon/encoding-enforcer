package com.miked.maven.enforcer.rule;

import java.io.File;
import java.io.FileInputStream;

import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;
import org.mozilla.universalchardet.UniversalDetector;

/**
 * Checks file encodings to see if they match the project.build.sourceEncoding
 *
 * If file encoding can not be determined it is skipped.
 *
 * @see https://github.com/mikedon/encoding-enforcer
 */
public class RequireEncoding implements EnforcerRule {
	/**
	 * Validate files match this encoding. If not specified then default to
	 * ${project.builder.sourceEncoding}.
	 */
	private String encoding = "";

	/**
	 * Comma (or pipe) separated list of globs do incluide.
	 */
	private String includes = "";

	/**
	 * Comma (or pipe) separated list of globs do exclude.
	 */
	private String excludes = "";

	/**
	 * Enables SCM files exclusions. Enabled by default.
	 */
	private boolean useDefaultExcludes = true;

	public void execute(EnforcerRuleHelper helper) throws EnforcerRuleException {
		try {
			if (StringUtils.isBlank(encoding)) {
				encoding = (String) helper.evaluate("${project.build.sourceEncoding}");
			}
			String basedir = (String) helper.evaluate("${basedir}");
			DirectoryScanner ds = new DirectoryScanner();
			ds.setBasedir(basedir);
			if (StringUtils.isNotBlank(includes)) {
				ds.setIncludes(includes.split("[,\\|]"));
			}
			if (StringUtils.isNotBlank(excludes)) {
				ds.setExcludes(excludes.split("[,\\|]"));
			}
			if (useDefaultExcludes) {
				ds.addDefaultExcludes();
			}
			ds.scan();
			Log log = helper.getLog();
			StringBuilder filesInMsg = new StringBuilder();
			for (String file : ds.getIncludedFiles()) {
				String fileEncoding = getEncoding(new File(basedir, file), log);
				if (log.isDebugEnabled()) {
					log.debug(file + "==>" + fileEncoding);
				}
				if (fileEncoding != null && !fileEncoding.equals(encoding)) {
					filesInMsg.append(file);
					filesInMsg.append("==>");
					filesInMsg.append(fileEncoding);
					filesInMsg.append("\n");
				}
			}
			if (filesInMsg.length() > 0) {
				throw new EnforcerRuleException("Files not encoded in " + encoding + ":\n" + filesInMsg);
			}
		} catch (ExpressionEvaluationException e) {
			throw new EnforcerRuleException("Unable to lookup an expression " + e.getLocalizedMessage(), e);
		}
	}

	protected String getEncoding(File file, Log log) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			UniversalDetector detector = new UniversalDetector(null);
			byte[] buf = new byte[4096];
			int nread;
			while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
				detector.handleData(buf, 0, nread);
			}
			detector.dataEnd();
			String encoding = detector.getDetectedCharset();
			detector.reset();
			return encoding;
		} catch (Exception e) {
			log.warn("Unable to detect encoding for file: " + file + " due to: " + e);
		} finally {
			IOUtil.close(fis);
		}
		return null;
	}

	/**
	 * If your rule is cacheable, you must return a unique id when parameters or
	 * conditions change that would cause the result to be different. Multiple
	 * cached results are stored based on their id.
	 *
	 * The easiest way to do this is to return a hash computed from the values
	 * of your parameters.
	 *
	 * If your rule is not cacheable, then the result here is not important, you
	 * may return anything.
	 */
	public String getCacheId() {
		return null;
	}

	/**
	 * This tells the system if the results are cacheable at all. Keep in mind
	 * that during forked builds and other things, a given rule may be executed
	 * more than once for the same project. This means that even things that
	 * change from project to project may still be cacheable in certain
	 * instances.
	 */
	public boolean isCacheable() {
		return false;
	}

	/**
	 * If the rule is cacheable and the same id is found in the cache, the
	 * stored results are passed to this method to allow double checking of the
	 * results. Most of the time this can be done by generating unique ids, but
	 * sometimes the results of objects returned by the helper need to be
	 * queried. You may for example, store certain objects in your rule and then
	 * query them later.
	 */
	public boolean isResultValid(EnforcerRule cachedRule) {
		return false;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getIncludes() {
		return includes;
	}

	public void setIncludes(String includes) {
		this.includes = includes;
	}

	public String getExcludes() {
		return excludes;
	}

	public void setExcludes(String excludes) {
		this.excludes = excludes;
	}

	public boolean isUseDefaultExcludes() {
		return useDefaultExcludes;
	}

	public void setUseDefaultExcludes(boolean useDefaultExcludes) {
		this.useDefaultExcludes = useDefaultExcludes;
	}
}
