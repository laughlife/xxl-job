package com.xxl.job.admin.controller.biz;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xxl.job.admin.constant.Consts;
import com.xxl.job.admin.mapper.XxlJobPythonMapper;
import com.xxl.job.admin.python.XxlJobPython;
import com.xxl.sso.core.annotation.XxlSso;
import com.xxl.tool.core.CollectionTool;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.response.PageModel;
import com.xxl.tool.response.Response;

import jakarta.annotation.Resource;

@Controller
@RequestMapping("/python")
public class PythonController {

    @Resource
    private XxlJobPythonMapper xxlJobPythonMapper;

    @RequestMapping
    @XxlSso(role = Consts.ADMIN_ROLE)
    public String index(Model model) {
        return "biz/python.list";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    @XxlSso(role = Consts.ADMIN_ROLE)
    public Response<PageModel<XxlJobPython>> pageList(@RequestParam(required = false, defaultValue = "0") int offset,
                                                      @RequestParam(required = false, defaultValue = "10") int pagesize,
                                                      @RequestParam(required = false) String name,
                                                      @RequestParam(required = false) String version) {

        List<XxlJobPython> list = xxlJobPythonMapper.pageList(offset, pagesize, name, version);
        int total = xxlJobPythonMapper.pageListCount(offset, pagesize, name, version);

        PageModel<XxlJobPython> pageModel = new PageModel<>();
        pageModel.setData(list);
        pageModel.setTotal(total);
        return Response.ofSuccess(pageModel);
    }

    @RequestMapping("/insert")
    @ResponseBody
    @XxlSso(role = Consts.ADMIN_ROLE)
    public Response<String> insert(XxlJobPython xxlJobPython) {
        Response<String> valid = validItem(xxlJobPython, false);
        if (!valid.isSuccess()) {
            return valid;
        }

        if (xxlJobPythonMapper.loadByVersion(xxlJobPython.getVersion().trim()) != null) {
            return Response.ofFail("非法");
        }

        Date now = new Date();
        xxlJobPython.setName(xxlJobPython.getName().trim());
        xxlJobPython.setVersion(xxlJobPython.getVersion().trim());
        xxlJobPython.setExecPath(xxlJobPython.getExecPath().trim());
        if (StringTool.isNotBlank(xxlJobPython.getRemark())) {
            xxlJobPython.setRemark(xxlJobPython.getRemark().trim());
        }
        xxlJobPython.setAddTime(now);
        xxlJobPython.setUpdateTime(now);

        int ret = xxlJobPythonMapper.save(xxlJobPython);
        return ret > 0 ? Response.ofSuccess() : Response.ofFail();
    }

    @RequestMapping("/update")
    @ResponseBody
    @XxlSso(role = Consts.ADMIN_ROLE)
    public Response<String> update(XxlJobPython xxlJobPython) {
        Response<String> valid = validItem(xxlJobPython, true);
        if (!valid.isSuccess()) {
            return valid;
        }

        XxlJobPython exist = xxlJobPythonMapper.loadById(xxlJobPython.getId());
        if (exist == null) {
            return Response.ofFail("非法");
        }

        String newVersion = xxlJobPython.getVersion().trim();
        if (!newVersion.equals(exist.getVersion())) {
            XxlJobPython sameVersion = xxlJobPythonMapper.loadByVersion(newVersion);
            if (sameVersion != null && sameVersion.getId() != exist.getId()) {
                return Response.ofFail("非法");
            }
        }

        exist.setName(xxlJobPython.getName().trim());
        exist.setVersion(newVersion);
        exist.setExecPath(xxlJobPython.getExecPath().trim());
        exist.setRemark(StringTool.isNotBlank(xxlJobPython.getRemark()) ? xxlJobPython.getRemark().trim() : null);
        exist.setUpdateTime(new Date());

        int ret = xxlJobPythonMapper.update(exist);
        return ret > 0 ? Response.ofSuccess() : Response.ofFail();
    }

    @RequestMapping("/delete")
    @ResponseBody
    @XxlSso(role = Consts.ADMIN_ROLE)
    public Response<String> delete(@RequestParam("ids[]") List<Integer> ids) {
        if (CollectionTool.isEmpty(ids) || ids.size() != 1) {
            return Response.ofFail("请选择" + "一条" + "数据");
        }
        int ret = xxlJobPythonMapper.delete(ids.get(0));
        return ret > 0 ? Response.ofSuccess() : Response.ofFail();
    }

    @RequestMapping("/scan")
    @ResponseBody
    @XxlSso(role = Consts.ADMIN_ROLE)
    public Response<String> scan() {
        List<XxlJobPython> created = new ArrayList<>();
        try {
            Set<String> execPaths = new LinkedHashSet<>(scanCandidatePythonExecPaths());
            if (execPaths.isEmpty()) {
                return Response.ofFail("失败");
            }

            Date now = new Date();
            for (String execPath : execPaths) {
                String version = resolvePythonVersion(execPath);
                if (StringTool.isBlank(version)) {
                    continue;
                }
                if (xxlJobPythonMapper.loadByVersion(version) != null) {
                    continue;
                }

                XxlJobPython item = new XxlJobPython();
                item.setName("Python " + version);
                item.setVersion(version);
                item.setExecPath(execPath);
                item.setRemark(null);
                item.setAddTime(now);
                item.setUpdateTime(now);

                if (xxlJobPythonMapper.save(item) > 0) {
                    created.add(item);
                }
            }

            return Response.ofSuccess(String.valueOf(created.size()));
        } catch (Exception e) {
            return Response.ofFail(e.getMessage());
        }
    }

    private Response<String> validItem(XxlJobPython item, boolean checkId) {
        if (item == null) {
            return Response.ofFail("非法");
        }
        if (checkId && item.getId() <= 0) {
            return Response.ofFail("非法");
        }
        if (StringTool.isBlank(item.getName())) {
            return Response.ofFail("请输入" + "名称");
        }
        if (StringTool.isBlank(item.getVersion())) {
            return Response.ofFail("请输入" + "版本");
        }
        if (StringTool.isBlank(item.getExecPath())) {
            return Response.ofFail("请输入" + "可执行路径");
        }
        if (item.getName().trim().length() > 64) {
            return Response.ofFail("长度限制" + "[1-64]");
        }
        if (item.getVersion().trim().length() > 32) {
            return Response.ofFail("长度限制" + "[1-32]");
        }
        if (item.getExecPath().trim().length() > 512) {
            return Response.ofFail("长度限制" + "[1-512]");
        }
        if (StringTool.isNotBlank(item.getRemark()) && item.getRemark().trim().length() > 255) {
            return Response.ofFail("长度限制" + "[0-255]");
        }
        return Response.ofSuccess();
    }

    private List<String> scanCandidatePythonExecPaths() throws Exception {
        String osName = System.getProperty("os.name");
        boolean windows = osName != null && osName.toLowerCase(Locale.ROOT).contains("win");

        List<String> execPaths = new ArrayList<>();
        if (windows) {
            execPaths.addAll(parsePyLauncherPaths(execCommand(new String[]{"cmd", "/c", "py", "-0p"})));
        } else {
            execPaths.addAll(parseWhichPaths(execCommand(new String[]{"sh", "-c", "which -a python3 python 2>/dev/null || true"})));
        }
        return execPaths;
    }

    private List<String> parsePyLauncherPaths(String output) {
        if (output == null) {
            return List.of();
        }
        String[] lines = output.split("\\r?\\n");
        List<String> paths = new ArrayList<>();
        for (String line : lines) {
            String trimmed = line == null ? "" : line.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            String[] parts = trimmed.split("\\s+");
            String last = parts[parts.length - 1];
            if (last.toLowerCase(Locale.ROOT).endsWith(".exe")) {
                paths.add(last);
            }
        }
        return paths;
    }

    private List<String> parseWhichPaths(String output) {
        if (output == null) {
            return List.of();
        }
        String[] lines = output.split("\\r?\\n");
        List<String> paths = new ArrayList<>();
        for (String line : lines) {
            String trimmed = line == null ? "" : line.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            paths.add(trimmed);
        }
        return paths;
    }

    private String resolvePythonVersion(String execPath) throws Exception {
        String output = execCommand(new String[]{execPath, "--version"});
        if (StringTool.isBlank(output)) {
            return null;
        }
        String line = output.trim().split("\\r?\\n")[0].trim();
        if (!line.toLowerCase(Locale.ROOT).startsWith("python")) {
            return null;
        }
        String[] parts = line.split("\\s+");
        if (parts.length < 2) {
            return null;
        }
        return parts[1].trim();
    }

    private String execCommand(String[] cmd) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(cmd);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        int exitCode = process.waitFor();
        if (exitCode != 0 && sb.length() == 0) {
            throw new IllegalStateException("exitCode=" + exitCode);
        }
        return sb.toString();
    }
}
